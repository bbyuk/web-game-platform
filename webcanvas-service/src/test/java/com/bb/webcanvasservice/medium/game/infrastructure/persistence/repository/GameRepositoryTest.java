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

}
