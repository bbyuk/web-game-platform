package com.bb.webcanvasservice.medium.game.infrastructure.persistence.repository;

import com.bb.webcanvasservice.common.util.FingerprintGenerator;
import com.bb.webcanvasservice.common.util.JoinCodeGenerator;
import com.bb.webcanvasservice.config.JpaConfig;
import com.bb.webcanvasservice.game.application.repository.GameRoomRepository;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoom;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoomParticipant;
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

import java.util.Optional;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@Import({JpaConfig.class, GameRoomRepositoryImpl.class, UserRepositoryImpl.class})
@DisplayName("[medium] [game] [persistence] Game Repository 영속성 테스트")
public class GameRepositoryTest {


    @Autowired
    private GameRoomRepository gameRoomRepository;

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
    @DisplayName("JoinCode를 사용할 수 있는지 여부를 체크 - 성공 테스트")
    void JoinCode를_사용할_수_있는지_여부를_체크_1() throws Exception {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("JoinCode를 사용할 수 있는지 여부를 체크 - 동시에 여러 스레드가 같은 JoinCode를 생성해 경합하는 RaceCondition 테스트")
    void JoinCode를_사용할_수_있는지_여부를_체크_2() throws Exception {
        // given

        // when

        // then
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

        // when

        // then
    }

    @Test
    @DisplayName("게임 방 저장 - GameRoomParticipant, GameSession, GameTurn 없는 case")
    void 게임_방_저장_1() throws Exception {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("게임 방 저장 - GameSession, GameTurn 없는 case")
    void 게임_방_저장_2() throws Exception {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("게임 방 저장 - GameTurn 없는 case")
    void 게임_방_저장_3() throws Exception {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("게임 방 저장 - 애그리거트 내 전체 도메인 모델 저장")
    void 게임_방_저장_4() throws Exception {
        // given

        // when

        // then
    }
}
