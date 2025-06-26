package com.bb.webcanvasservice.medium.game.infrastructure.persistence.repository;

import com.bb.webcanvasservice.common.util.FingerprintGenerator;
import com.bb.webcanvasservice.common.util.JoinCodeGenerator;
import com.bb.webcanvasservice.config.JpaConfig;
import com.bb.webcanvasservice.game.application.repository.GameRoomRepository;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoom;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoomParticipant;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoomState;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameSession;
import com.bb.webcanvasservice.game.infrastructure.persistence.repository.GameRoomRepositoryImpl;
import com.bb.webcanvasservice.user.domain.model.User;
import com.bb.webcanvasservice.user.domain.repository.UserRepository;
import com.bb.webcanvasservice.user.infrastructure.persistence.repository.UserRepositoryImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@DataJpaTest
@Import({
        JpaConfig.class,
        GameRoomRepositoryImpl.class,
        UserRepositoryImpl.class,
        RaceConditionTester.class
})
@DisplayName("[medium] [game] [persistence] Game Repository 영속성 테스트")
public class GameRepositoryTest {


    @Autowired
    private GameRoomRepository gameRoomRepository;

    @Autowired
    private RaceConditionTester raceConditionTester;

    @Autowired
    private UserRepository userRepository;

    int joinCodeLength = 6;

    int roomCapacity = 5;


    @Test
    @DisplayName("게임방 ID로 게임 방 찾기 - 입장자, 게임 세션, 게임 턴 없는 경우")
    void 게임방_ID로_게임_방_찾기_1() throws Exception {
        // given
        GameRoom savedGameRoom = gameRoomRepository.save(GameRoom.create(JoinCodeGenerator.generate(joinCodeLength), roomCapacity));

        // when
        gameRoomRepository.findGameRoomById(savedGameRoom.getId())
                .ifPresent(findGameRoom -> {
                    Assertions.assertThat(findGameRoom).usingRecursiveComparison().isEqualTo(savedGameRoom);
                });

        // then

    }

    @Test
    @DisplayName("게임 방 ID로 게임 방 찾기 - 입장자 캐스케이드")
    void 게임방_ID로_게임_방_찾기_2() throws Exception {
        // given
        User hostUser = userRepository.save(User.create(FingerprintGenerator.generate()));
        User guestUser = userRepository.save(User.create(FingerprintGenerator.generate()));

        GameRoom savedGameRoom = gameRoomRepository.save(GameRoom.create(JoinCodeGenerator.generate(joinCodeLength), roomCapacity));

        GameRoomParticipant host = GameRoomParticipant.create(hostUser.getId(), "호스트");
        GameRoomParticipant guest = GameRoomParticipant.create(guestUser.getId(), "게스트");

        savedGameRoom.letIn(host);
        savedGameRoom.letIn(guest);

        gameRoomRepository.save(savedGameRoom);

        // when

        // then
    }

    @Test
    @DisplayName("게임 방 ID로 게임 방 찾기 - 세션 캐스케이드")
    void 게임방_ID로_게임_방_찾기_3() throws Exception {
        // given
        User hostUser = userRepository.save(User.create(FingerprintGenerator.generate()));
        User guestUser = userRepository.save(User.create(FingerprintGenerator.generate()));

        GameRoom gameRoom = GameRoom.create(JoinCodeGenerator.generate(joinCodeLength), roomCapacity);

        GameRoomParticipant host = GameRoomParticipant.create(hostUser.getId(), "호스트");
        GameRoomParticipant guest = GameRoomParticipant.create(guestUser.getId(), "게스트");

        gameRoom.letIn(host);
        gameRoom.letIn(guest);

        gameRoom.changeParticipantReady(guest, true);

        gameRoom.loadGameSession(2);

        GameRoom savedGameRoom = gameRoomRepository.save(gameRoom);

        // when
        Assertions.assertThat(savedGameRoom.getGameSession()).isNotNull();
        gameRoomRepository.findGameRoomById(savedGameRoom.getId())
                .ifPresent(findGameRoom -> {
                    Assertions.assertThat(findGameRoom).usingRecursiveComparison().isEqualTo(savedGameRoom);
                });

        // then
    }

    @Test
    @DisplayName("게임 방 ID로 게임 방 찾기 - 턴, 세션 캐스케이드")
    void 게임_방_ID로_게임_방_찾기() throws Exception {
        // given
        User hostUser = userRepository.save(User.create(FingerprintGenerator.generate()));
        User guestUser = userRepository.save(User.create(FingerprintGenerator.generate()));

        GameRoom gameRoom = GameRoom.create(JoinCodeGenerator.generate(joinCodeLength), roomCapacity);

        GameRoomParticipant host = GameRoomParticipant.create(hostUser.getId(), "호스트");
        GameRoomParticipant guest = GameRoomParticipant.create(guestUser.getId(), "게스트");

        gameRoom.letIn(host);
        gameRoom.letIn(guest);

        gameRoom.changeParticipantReady(guest, true);

        gameRoom.loadGameSession(2);

        GameSession gameSession = gameRoom.getGameSession();
        gameSession.processToNextTurn(guestUser.getId(), "테스트");

        GameRoom savedGameRoom = gameRoomRepository.save(gameRoom);

        // when
        Assertions.assertThat(gameSession.getGameTurns().size()).isEqualTo(1);
        gameRoomRepository.findGameRoomById(savedGameRoom.getId())
                .ifPresent(findGameRoom -> {
                    Assertions.assertThat(findGameRoom).usingRecursiveComparison().isEqualTo(savedGameRoom);
                });


        // then
    }

    @Test
    @DisplayName("게임 방 입장자 ID로 조회 - 성공 테스트")
    void 게임_방_입장자_ID로_조회() throws Exception {
        // given
        User hostUser = userRepository.save(User.create(FingerprintGenerator.generate()));
        User guestUser = userRepository.save(User.create(FingerprintGenerator.generate()));

        GameRoom gameRoom = GameRoom.create(JoinCodeGenerator.generate(joinCodeLength), roomCapacity);

        GameRoomParticipant host = GameRoomParticipant.create(hostUser.getId(), "호스트");
        GameRoomParticipant guest = GameRoomParticipant.create(guestUser.getId(), "게스트");

        gameRoom.letIn(host);
        gameRoom.letIn(guest);

        GameRoom savedGameRoom = gameRoomRepository.save(gameRoom);
        GameRoomParticipant findHostParticipant = savedGameRoom.getParticipants()
                .stream()
                .filter(participant -> participant.getUserId().equals(hostUser.getId()))
                .findFirst()
                .orElseThrow(IllegalAccessError::new);

        GameRoomParticipant findGuestParticipant = savedGameRoom.getParticipants()
                .stream()
                .filter(participant -> participant.getUserId().equals(guestUser.getId()))
                .findFirst()
                .orElseThrow(IllegalAccessError::new);

        // when
        GameRoom findGameRoomByHost = gameRoomRepository.findGameRoomByGameRoomParticipantId(findHostParticipant.getId())
                .orElseThrow(RuntimeException::new);
        GameRoom findGameRoomByGuest = gameRoomRepository.findGameRoomByGameRoomParticipantId(findGuestParticipant.getId())
                .orElseThrow(RuntimeException::new);

        // then
        Assertions.assertThat(findGameRoomByHost).usingRecursiveComparison().isEqualTo(savedGameRoom);
        Assertions.assertThat(findGameRoomByHost).usingRecursiveComparison().isEqualTo(findGameRoomByGuest);
    }

    @Test
    @DisplayName("게임 세션 ID로 조회 - 성공 테스트")
    void 게임_세션_ID로_조회() throws Exception {
        // given
        User hostUser = userRepository.save(User.create(FingerprintGenerator.generate()));
        User guestUser = userRepository.save(User.create(FingerprintGenerator.generate()));

        GameRoom gameRoom = GameRoom.create(JoinCodeGenerator.generate(joinCodeLength), roomCapacity);

        GameRoomParticipant host = GameRoomParticipant.create(hostUser.getId(), "호스트");
        GameRoomParticipant guest = GameRoomParticipant.create(guestUser.getId(), "게스트");

        gameRoom.letIn(host);
        gameRoom.letIn(guest);

        gameRoom.changeParticipantReady(guest, true);

        gameRoom.loadGameSession(22);

        GameRoom savedGameRoom = gameRoomRepository.save(gameRoom);

        // when
        GameRoom findGameRoom = gameRoomRepository.findGameRoomByGameSessionId(savedGameRoom.getGameSession().getId())
                .orElseThrow(RuntimeException::new);

        // then
        Assertions.assertThat(findGameRoom).usingRecursiveComparison().isEqualTo(savedGameRoom);
    }

    @Test
    @DisplayName("유저 ID로 대상 유저가 입장해 있는 방 조회 - 성공 테스트")
    void 유저ID로_대상_유저가_입장해_있는_방_조회() throws Exception {
        // given
        User hostUser = userRepository.save(User.create(FingerprintGenerator.generate()));
        User guestUser = userRepository.save(User.create(FingerprintGenerator.generate()));

        Long wrongUserId = 2321L;

        GameRoom gameRoom = GameRoom.create(JoinCodeGenerator.generate(joinCodeLength), roomCapacity);

        GameRoomParticipant host = GameRoomParticipant.create(hostUser.getId(), "호스트");
        GameRoomParticipant guest = GameRoomParticipant.create(guestUser.getId(), "게스트");

        gameRoom.letIn(host);
        gameRoom.letIn(guest);

        GameRoom savedGameRoom = gameRoomRepository.save(gameRoom);

        // when
        GameRoom findByHostUserId = gameRoomRepository.findCurrentJoinedGameRoomByUserId(host.getUserId())
                .orElseThrow(RuntimeException::new);

        GameRoom findByGuestUserId = gameRoomRepository.findCurrentJoinedGameRoomByUserId(guest.getUserId())
                .orElseThrow(RuntimeException::new);

        // then
        Assertions.assertThat(findByHostUserId).usingRecursiveComparison().isEqualTo(savedGameRoom);
        Assertions.assertThat(findByGuestUserId).usingRecursiveComparison().isEqualTo(savedGameRoom);
        Assertions.assertThat(gameRoomRepository.findCurrentJoinedGameRoomByUserId(wrongUserId)).isEmpty();

    }

    @Test
    @DisplayName("JoinCode를 사용할 수 있는지 여부를 체크 - Active 상태인 방들 중 파라미터로 넘어온 JoinCode를 사용하는 방이 없어야 한다.")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void JoinCode를_사용할_수_있는지_여부를_체크_1() throws Exception {
        // given

        String joinCode = JoinCodeGenerator.generate(joinCodeLength);
        GameRoom gameRoom = GameRoom.create(joinCode, roomCapacity);
        gameRoom.close();

        gameRoomRepository.save(gameRoom);
        // when
        Assertions.assertThat(gameRoomRepository.existsJoinCodeConflictOnActiveGameRoom(joinCode))
                .isEqualTo(false);

        // then
    }


    @Test
    @DisplayName("JoinCode를 사용할 수 있는지 여부를 체크 - 동시에 여러 스레드가 같은 JoinCode를 생성해 경합하는 RaceCondition 테스트")
    void JoinCode를_사용할_수_있는지_여부를_체크_2() throws Exception {
        // given
        String raceConditionJoinCode = JoinCodeGenerator.generate(joinCodeLength);

        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        List<Future<?>> futures = new ArrayList<>();
        AtomicInteger joinCodeUsingCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            futures.add(executor.submit(() -> {
                try {
                    readyLatch.countDown(); // 준비 완료 알림
                    startLatch.await();     // 모든 스레드가 준비될 때까지 대기

                    // 👇 여기서 race condition 유도하고 싶은 로직 실행
                    raceConditionTester.joinCodeRaceCondition(raceConditionJoinCode, roomCapacity, joinCodeUsingCount);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            }));
        }

        // when
        // 모든 스레드가 준비될 때까지 기다림
        readyLatch.await();
        // 동시에 시작
        startLatch.countDown();

        // 완료 대기
        doneLatch.await();
        executor.shutdown();

        // then
        Assertions.assertThat(joinCodeUsingCount.get()).isEqualTo(1);
    }

    @Test
    @DisplayName("게임 방 정원, 상태, 입장자 상태로 게임 방 찾기")
    void 게임_방_정원_상태_입장자_상태로_게임_방_찾기() throws Exception {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("JoinCode로 게임 방 찾기")
    void JoinCode로_게임_방_찾기() throws Exception {
        // given
        String cannotUseJoinCode = JoinCodeGenerator.generate(joinCodeLength);
        String canUseJoinCode = JoinCodeGenerator.generate(joinCodeLength);

        GameRoom gameRoom = GameRoom.create(cannotUseJoinCode, roomCapacity);
        GameRoom closedRoom = GameRoom.create(canUseJoinCode, roomCapacity);
        closedRoom.close();

        gameRoomRepository.save(gameRoom);
        gameRoomRepository.save(closedRoom);

        // when
        Assertions.assertThat(
                        gameRoomRepository
                                .findGameRoomByJoinCodeAndState(
                                        cannotUseJoinCode,
                                        GameRoomState.WAITING
                                )
                )
                .isNotEmpty();
        Assertions.assertThat(
                gameRoomRepository
                        .findGameRoomByJoinCodeAndState(
                                canUseJoinCode,
                                GameRoomState.WAITING
                        )
        ).isEmpty();

        // then
    }

    @Test
    @DisplayName("게임 방 저장")
    void 게임_방_저장_1() throws Exception {
        // given
        GameRoom gameRoom = GameRoom.create(JoinCodeGenerator.generate(joinCodeLength), roomCapacity);


        // when
        GameRoom savedGameRoom = gameRoomRepository.save(gameRoom);

        // then
        Assertions.assertThat(gameRoom)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(savedGameRoom);

    }
}
