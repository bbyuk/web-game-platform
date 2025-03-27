package com.bb.webcanvasservice.unit.domain.game.repository;

import com.bb.webcanvasservice.common.RandomCodeGenerator;
import com.bb.webcanvasservice.domain.game.GameRoom;
import com.bb.webcanvasservice.domain.game.GameRoomEntrance;
import com.bb.webcanvasservice.domain.game.enums.GameRoomState;
import com.bb.webcanvasservice.domain.game.repository.GameRoomEntranceRepository;
import com.bb.webcanvasservice.domain.game.repository.GameRoomRepository;
import com.bb.webcanvasservice.domain.user.User;
import com.bb.webcanvasservice.domain.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest // JPA 관련 컴포넌트만 로드하여 테스트
class GameRepositoryTest {

    @Autowired
    private GameRoomRepository gameRoomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRoomEntranceRepository gameRoomEntranceRepository;

    private final String testUserToken = UUID.randomUUID().toString();

    private User testUser;

    @BeforeEach
    public void setTestData() {
        testUser = userRepository.save(new User(testUserToken));
    }


    @Test
    @DisplayName("유저 토큰으로 유저가 현재 입장한 방 조회 - 데이터 존재할 시 성공")
    public void findNotClosedRoomByUserToken() {
        // given
        String enteredRoomCode = RandomCodeGenerator.generate(10);
        String otherRoomCode = RandomCodeGenerator.generate(10);

        GameRoom enteredRoom = new GameRoom(GameRoomState.WAITING, enteredRoomCode);
        GameRoom otherRoom = new GameRoom(GameRoomState.WAITING, otherRoomCode);

        gameRoomRepository.save(enteredRoom);
        gameRoomRepository.save(otherRoom);

        GameRoomEntrance gameRoomEntrance = new GameRoomEntrance(enteredRoom, testUser);
        gameRoomEntranceRepository.save(gameRoomEntrance);

        enteredRoom.addEntrance(gameRoomEntrance);

        // when
        Optional<GameRoom> queryResult = gameRoomRepository.findNotClosedGameRoomByUserId(testUser.getId());

        // then
        Assertions.assertThat(queryResult.isPresent()).isTrue();
        Assertions.assertThat(queryResult.get().getJoinCode()).isEqualTo(enteredRoomCode);
    }

    @Test
    @DisplayName("활성 상태인 게임 방의 입장 코드와 충돌이 발생했을 때 true 리턴")
    public void returnTrueWhenJoinCodeConflict() {
        // given
        String testRoomCode = RandomCodeGenerator.generate(10);
        GameRoom playingGameRoom = new GameRoom(GameRoomState.PLAYING, testRoomCode);

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
        String joinCode = RandomCodeGenerator.generate(10);


        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        // when

        Runnable task = () -> {
            try {
                 // 비관적 락 획득
                if (!gameRoomRepository.existsJoinCodeConflictOnActiveGameRoom(joinCode)) {
                    GameRoom lockTakenGameRoom = new GameRoom(GameRoomState.WAITING, joinCode);
                    gameRoomRepository.save(lockTakenGameRoom);
                }

                Thread.sleep(3000); // 일부러 지연 -> 락 유지됨
            }
            catch (Exception e) {
                System.out.println("e = " + e);
                throw new IllegalStateException(e);
            }
            finally {
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

        GameRoom waitingRoom = new GameRoom(GameRoomState.WAITING, RandomCodeGenerator.generate(10));
        GameRoom playingRoom = new GameRoom(GameRoomState.PLAYING, RandomCodeGenerator.generate(10));

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

        GameRoom waitingRoom = new GameRoom(GameRoomState.WAITING, RandomCodeGenerator.generate(10));
        GameRoom playingRoom = new GameRoom(GameRoomState.PLAYING, RandomCodeGenerator.generate(10));

        gameRoomRepository.save(waitingRoom);
        gameRoomRepository.save(playingRoom);

        GameRoomEntrance gameRoomEntrance = new GameRoomEntrance(waitingRoom, testUser);
        gameRoomEntranceRepository.save(gameRoomEntrance);

        // when
        boolean result = gameRoomEntranceRepository.existsGameRoomEntranceByUserId(testUserId);

        // then
        Assertions.assertThat(result).isTrue();
    }
}