package com.bb.webcanvasservice.unit.domain.game.repository;

import com.bb.webcanvasservice.common.util.JoinCodeGenerator;
import com.bb.webcanvasservice.common.config.JpaConfig;
import com.bb.webcanvasservice.game.infrastructure.persistence.entity.GameRoomJpaEntity;
import com.bb.webcanvasservice.game.infrastructure.persistence.entity.GameRoomEntranceJpaEntity;
import com.bb.webcanvasservice.game.domain.model.GameRoomEntranceRole;
import com.bb.webcanvasservice.game.domain.model.GameRoomState;
import com.bb.webcanvasservice.game.infrastructure.persistence.repository.GameRoomEntranceJpaRepository;
import com.bb.webcanvasservice.game.infrastructure.persistence.repository.GameRoomJpaRepository;
import com.bb.webcanvasservice.user.infrastructure.persistence.entity.UserJpaEntity;
import com.bb.webcanvasservice.user.infrastructure.persistence.repository.UserJpaRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest // JPA 관련 컴포넌트만 로드하여 테스트
@Import(JpaConfig.class)
@DisplayName("[unit] [persistence] 게임 repository 단위테스트")
class GameRepositoryTest {

    @Autowired
    private GameRoomJpaRepository gameRoomRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private GameRoomEntranceJpaRepository gameRoomEntranceRepository;

    private final String testUserToken = UUID.randomUUID().toString();

    private UserJpaEntity testUser;

    @BeforeEach
    public void setTestData() {
        testUser = userJpaRepository.save(new UserJpaEntity(testUserToken));
    }


    @Test
    @DisplayName("유저 토큰으로 유저가 현재 입장한 방 조회 - 데이터 존재할 시 성공")
    public void findNotClosedRoomByUserToken() {
        // given
        String enteredRoomCode = JoinCodeGenerator.generate(10);
        String otherRoomCode = JoinCodeGenerator.generate(10);

        GameRoomJpaEntity enteredRoom = new GameRoomJpaEntity(GameRoomState.WAITING, enteredRoomCode);
        GameRoomJpaEntity otherRoom = new GameRoomJpaEntity(GameRoomState.WAITING, otherRoomCode);

        gameRoomRepository.save(enteredRoom);
        gameRoomRepository.save(otherRoom);

        GameRoomEntranceJpaEntity gameRoomEntrance = new GameRoomEntranceJpaEntity(enteredRoom, testUser, "테스트 호랑이", GameRoomEntranceRole.GUEST);
        gameRoomEntranceRepository.save(gameRoomEntrance);

        // when
        Optional<GameRoomJpaEntity> queryResult = gameRoomRepository.findNotClosedGameRoomByUserId(testUser.getId());

        // then
        Assertions.assertThat(queryResult.isPresent()).isTrue();
        Assertions.assertThat(queryResult.get().getJoinCode()).isEqualTo(enteredRoomCode);
    }

    @Test
    @DisplayName("활성 상태인 게임 방의 입장 코드와 충돌이 발생했을 때 true 리턴")
    public void returnTrueWhenJoinCodeConflict() {
        // given
        String testRoomCode = JoinCodeGenerator.generate(10);
        GameRoomJpaEntity playingGameRoom = new GameRoomJpaEntity(GameRoomState.PLAYING, testRoomCode);

        gameRoomRepository.save(playingGameRoom);

        // when
        boolean result = gameRoomRepository.existsJoinCodeConflictOnActiveGameRoom(testRoomCode);

        // then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    @Transactional
    @DisplayName("동시에 두 유저가 게임 방 생성 요청 - 같은 게임 입장 코드로 게임 방 생성 쿼리를 실행할 경우 락을 획득하지 못한 유저는 Exception 발생")
    public void gameRoomJoinCodeConflict() throws Exception {
        // given
        String joinCode = JoinCodeGenerator.generate(10);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        // when

        Runnable task = () -> {
            try {
                // 비관적 락 획득
                if (!gameRoomRepository.existsJoinCodeConflictOnActiveGameRoom(joinCode)) {
                    GameRoomJpaEntity lockTakenGameRoom = new GameRoomJpaEntity(GameRoomState.WAITING, joinCode);
                    gameRoomRepository.save(lockTakenGameRoom);
                }

                Thread.sleep(3000); // 일부러 지연 -> 락 유지됨
            } catch (Exception e) {
                System.out.println("e = " + e);
                throw new IllegalStateException(e);
            } finally {
                latch.countDown();
            }
        };

        // 두 개의 스레드 동시 실행
        executor.submit(task); // 첫 번째 스레드 실행 (락 획득 + insert)

        Thread.sleep(500); // 약간의 지연을 주어 락이 걸릴 시간을 확보

        executor.submit(task); // 두 번째 스레드 실행 (락이 걸린 상태에서 대기)

        // then
        latch.await(); // 모든 스레드가 끝날때까지 대기
        executor.shutdown();
    }

    @Test
    @DisplayName("쿼리 요청 유저가 입장한 방이 있다면 true 리턴")
    public void returnTrueWhenUserEnteredAnyRoom() {
        // given
        Long testUserId = testUser.getId();

        GameRoomJpaEntity waitingRoom = new GameRoomJpaEntity(GameRoomState.WAITING, JoinCodeGenerator.generate(10));
        GameRoomJpaEntity playingRoom = new GameRoomJpaEntity(GameRoomState.PLAYING, JoinCodeGenerator.generate(10));

        gameRoomRepository.save(waitingRoom);
        gameRoomRepository.save(playingRoom);

        // when
        Assertions.assertThat(gameRoomEntranceRepository.existsGameRoomEntranceByUserId(testUserId)).isFalse();

        // then

    }

    @Test
    @DisplayName("쿼리 요청 유저가 입장한 방이 없다면 false 리턴")
    public void returnFalseWhenUserNotEnteredAnyRoom() {
        // given
        Long testUserId = testUser.getId();

        GameRoomJpaEntity waitingRoom = new GameRoomJpaEntity(GameRoomState.WAITING, JoinCodeGenerator.generate(10));
        GameRoomJpaEntity playingRoom = new GameRoomJpaEntity(GameRoomState.PLAYING, JoinCodeGenerator.generate(10));

        gameRoomRepository.save(waitingRoom);
        gameRoomRepository.save(playingRoom);

        GameRoomEntranceJpaEntity gameRoomEntrance = new GameRoomEntranceJpaEntity(waitingRoom, testUser, "테스트 여우", GameRoomEntranceRole.GUEST);
        gameRoomEntranceRepository.save(gameRoomEntrance);

        // when
        boolean result = gameRoomEntranceRepository.existsGameRoomEntranceByUserId(testUserId);

        // then
        Assertions.assertThat(result).isTrue();
    }
}