package com.bb.webcanvasservice.integration.domain.game;

import com.bb.webcanvasservice.common.RandomCodeGenerator;
import com.bb.webcanvasservice.domain.game.GameRoom;
import com.bb.webcanvasservice.domain.game.GameRoomEntrance;
import com.bb.webcanvasservice.domain.game.GameRoomService;
import com.bb.webcanvasservice.domain.game.dto.response.GameRoomEntranceResponse;
import com.bb.webcanvasservice.domain.game.enums.GameRoomState;
import com.bb.webcanvasservice.domain.game.exception.AlreadyEnteredRoomException;
import com.bb.webcanvasservice.domain.game.exception.IllegalGameRoomStateException;
import com.bb.webcanvasservice.domain.game.repository.GameRoomEntranceRepository;
import com.bb.webcanvasservice.domain.game.repository.GameRoomRepository;
import com.bb.webcanvasservice.domain.user.User;
import com.bb.webcanvasservice.domain.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.*;

@Transactional
@SpringBootTest
class GameRoomServiceIntegrationTest {

    @Autowired
    private GameRoomService gameRoomService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRoomRepository gameRoomRepository;

    @Autowired
    private GameRoomEntranceRepository gameRoomEntranceRepository;

    private User testUser;
    private User waitingRoomHost;
    private User playingRoomHost;
    private GameRoom waitingRoom;
    private GameRoom playingRoom;

    @BeforeEach
    public void beforeEach() {
        // 테스트 공통 유저 저장
        testUser = userRepository.save(new User(UUID.randomUUID().toString()));
        waitingRoomHost = userRepository.save(new User(UUID.randomUUID().toString()));
        playingRoomHost = userRepository.save(new User(UUID.randomUUID().toString()));

        // 테스트 공통 게임 방 저장
        waitingRoom = gameRoomRepository.save(new GameRoom(GameRoomState.WAITING, RandomCodeGenerator.generate(10)));
        playingRoom = gameRoomRepository.save(new GameRoom(GameRoomState.PLAYING, RandomCodeGenerator.generate(10)));

        // 테스트 공통 방 호스트 입장
        gameRoomEntranceRepository.save(new GameRoomEntrance(waitingRoom, waitingRoomHost));
        gameRoomEntranceRepository.save(new GameRoomEntrance(playingRoom, playingRoomHost));
    }

    @Test
    @DisplayName("게임 방 입장 - 상태가 WAITING인 방에 입장 요청시 성공")
    public void enterGameRoom() {
        // given - 공통 Entity 사용

        // when
        GameRoomEntranceResponse gameRoomEntranceResponse = gameRoomService.enterGameRoom(waitingRoom.getId(), testUser.getId());

        // then
        Assertions.assertThat(gameRoomEntranceResponse.gameRoomId()).isEqualTo(waitingRoom.getId());
        Assertions.assertThat(gameRoomEntranceResponse.gameRoomEntranceId()).isNotNull();
    }

    @Test
    @DisplayName("게임 방 입장 - 상태가 PLAYING인 방에 입장 요청시 실패")
    public void enterGameRoomFailWhenTargetGameRoomStateIsPlaying() {
        // given - 공통 Entity 사용

        // when
        Assertions.assertThatThrownBy(() -> gameRoomService.enterGameRoom(playingRoom.getId(), testUser.getId()))
                .isInstanceOf(IllegalGameRoomStateException.class);

        // then
    }

    @Test
    @DisplayName("게임 방 입장 - 요청 유저가 현재 게임 방에 입장해있을 시 실패")
    public void enterGameRoomFailedWhenUserAlreadyEnterAnyGameRoom() {
        // given - 다른 방에 이미 입장해 있는 경우
        GameRoom anotherGameRoom = new GameRoom(GameRoomState.WAITING, RandomCodeGenerator.generate(10));
        gameRoomRepository.save(anotherGameRoom);
        gameRoomEntranceRepository.save(new GameRoomEntrance(anotherGameRoom, testUser));

        // when
        Assertions.assertThatThrownBy(() -> gameRoomService.enterGameRoom(waitingRoom.getId(), testUser.getId()))
                .isInstanceOf(AlreadyEnteredRoomException.class);

        // then
    }

    @Test
    @DisplayName("게임 방 생성 - 게임 방 생성 시 호스트로 생성된 게임 방에 자동 입장된다.")
    public void createGameRoom() {
        // given - 테스트 공통 유저 사용

        // when
        Long gameRoomId = gameRoomService.createGameRoom(testUser.getId());

        // then
        Assertions.assertThat(gameRoomId).isNotNull();
    }

    @Test
    @DisplayName("GameRoom JoinCode 검증 - 동시에 두 유저가 게임 방을 생성할 때 동일한 JoinCode로 GameRoom을 생성하려는 경우 먼저 실행된 스레드에서 생성된 joinCode가 사용된다.")
    public void verifyJoinCode() throws Exception {
        // given

        // 동시에 생성된 동일한 코드
        String joinCode = RandomCodeGenerator.generate(10);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        // when
        Callable<String> task = () -> {
            try {
                String verifiedJoinCode = gameRoomService.verifyJoinCode(joinCode);

                // verify된 joinCode로 방을 먼저 생성한다. (단순테스트 로직)
                gameRoomRepository.save(new GameRoom(GameRoomState.WAITING, verifiedJoinCode));

                Thread.sleep(3000); // 일부러 지연 -> 락 유지됨
                return verifiedJoinCode;
            }
            catch (Exception e) {
                System.out.println("e = " + e);
                throw new IllegalStateException(e);
            }
            finally {
                latch.countDown();
            }
        };

        // then
        // 두 개의 스레드 동시 실행
        Future<String> firstThreadResult = executor.submit(task);// 첫 번째 스레드 실행 (락 획득 + insert)

        Thread.sleep(500); // 약간의 지연을 주어 락이 걸릴 시간을 확보

        Future<String> secondThreadResult = executor.submit(task);// 두 번째 스레드 실행 (락이 걸린 상태에서 대기)

        // then
        latch.await(); // 모든 스레드가 끝날때까지 대기
        executor.shutdown();

        Assertions.assertThat(firstThreadResult.get()).isNotEqualTo(secondThreadResult.get());
    }

}