package com.bb.webcanvasservice.medium.game.infrastructure.persistence.repository;

import com.bb.webcanvasservice.common.util.FingerprintGenerator;
import com.bb.webcanvasservice.common.util.JoinCodeGenerator;
import com.bb.webcanvasservice.config.JpaConfig;
import com.bb.webcanvasservice.game.domain.model.room.GameRoom;
import com.bb.webcanvasservice.game.domain.model.room.GameRoomParticipant;
import com.bb.webcanvasservice.game.domain.model.session.GamePlayer;
import com.bb.webcanvasservice.game.domain.model.session.GameSession;
import com.bb.webcanvasservice.game.domain.repository.GameRoomRepository;
import com.bb.webcanvasservice.game.domain.repository.GameSessionRepository;
import com.bb.webcanvasservice.game.infrastructure.persistence.repository.GameRoomRepositoryImpl;
import com.bb.webcanvasservice.game.infrastructure.persistence.repository.GameSessionRepositoryImpl;
import com.bb.webcanvasservice.user.domain.model.User;
import com.bb.webcanvasservice.user.domain.repository.UserRepository;
import com.bb.webcanvasservice.user.infrastructure.persistence.repository.UserRepositoryImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@Import({
        JpaConfig.class,
        GameSessionRepositoryImpl.class,
        GameRoomRepositoryImpl.class,
        UserRepositoryImpl.class
})
@Transactional
@Tag("medium")
@DisplayName("[medium] [game/session] [persistence] Game Session Repository 영속성 테스트")
public class GameSessionRepositoryTest {

    @Autowired GameSessionRepository gameSessionRepository;

    @Autowired
    GameRoomRepository gameRoomRepository;

    @Autowired
    UserRepository userRepository;

    final int JOIN_CODE_LENGTH = 6;
    final int GAME_ROOM_CAPACITY = 8;

    @Test
    @DisplayName("게임 세션 저장 - 게임 세션에 포함된 플레이어 목록과 턴 목록도 함께 저장한다.")
    void 게임_세션_저장() throws Exception {
        // given
        User host = userRepository.save(User.create(FingerprintGenerator.generate()));
        User guest = userRepository.save(User.create(FingerprintGenerator.generate()));

        GameRoom gameRoom = GameRoom.create(JoinCodeGenerator.generate(JOIN_CODE_LENGTH), GAME_ROOM_CAPACITY);

        GameRoomParticipant hostParticipant = GameRoomParticipant.create(host.id(), "방장");
        GameRoomParticipant guestParticipant = GameRoomParticipant.create(guest.id(), "유저1");

        gameRoom.letIn(hostParticipant);
        gameRoom.letIn(guestParticipant);

        gameRoomRepository.save(gameRoom);

        gameRoom.changeParticipantReady(guestParticipant, true);

        gameRoom.validateStateToLoad();
        gameRoom.changeStateToPlay();

        GameRoom savedGameRoom = gameRoomRepository.save(gameRoom);


        // when
        GameSession savedGameSession = gameSessionRepository.save(GameSession.create(savedGameRoom.getId(), 2, 20));


        // then
        Assertions.assertThat(savedGameSession.id()).isNotNull();

        savedGameSession.involvePlayers(
                gameRoom.getCurrentParticipants().stream()
                        .map(gameRoomParticipant ->
                                GamePlayer.create(
                                        savedGameSession.id(),
                                        gameRoomParticipant.getUserId(),
                                        gameRoomParticipant.getNickname()
                                )
                        )
                        .collect(Collectors.toList())
        );
        GameSession gamePlayerSavedGameSession = gameSessionRepository.save(savedGameSession);

        Assertions.assertThat(gamePlayerSavedGameSession.gamePlayers().size()).isEqualTo(2);

    }


    @Test
    @DisplayName("게임 세션 ID로 게임 세션 조회 - 게임 턴, 게임 플레이어 목록 함꼐 로딩")
    void 게임_세션_ID로_게임_세션_조회() throws Exception {
        // given
        User host = userRepository.save(User.create(FingerprintGenerator.generate()));
        User guest = userRepository.save(User.create(FingerprintGenerator.generate()));

        GameRoom gameRoom = GameRoom.create(JoinCodeGenerator.generate(JOIN_CODE_LENGTH), GAME_ROOM_CAPACITY);

        GameRoomParticipant hostParticipant = GameRoomParticipant.create(host.id(), "방장");
        GameRoomParticipant guestParticipant = GameRoomParticipant.create(guest.id(), "유저1");

        gameRoom.letIn(hostParticipant);
        gameRoom.letIn(guestParticipant);

        gameRoomRepository.save(gameRoom);

        gameRoom.changeParticipantReady(guestParticipant, true);

        gameRoom.validateStateToLoad();
        gameRoom.changeStateToPlay();

        GameRoom savedGameRoom = gameRoomRepository.save(gameRoom);


        // when
        GameSession savedGameSession = gameSessionRepository.save(GameSession.create(savedGameRoom.getId(), 2, 20));
        savedGameSession.involvePlayers(
                gameRoom.getCurrentParticipants().stream()
                        .map(gameRoomParticipant ->
                                GamePlayer.create(
                                        savedGameSession.id(),
                                        gameRoomParticipant.getUserId(),
                                        gameRoomParticipant.getNickname()
                                )
                        )
                        .collect(Collectors.toList())
        );
        savedGameSession.start();
        savedGameSession.allocateNewGameTurn("hello world");



        GameSession gamePlayerSavedGameSession = gameSessionRepository.save(savedGameSession);



        // then
        Optional<GameSession> gameSessionById = gameSessionRepository.findGameSessionById(gamePlayerSavedGameSession.id());
        Assertions.assertThat(gameSessionById).isPresent();
        GameSession gameSession = gameSessionById.get();

        // game players
        Assertions.assertThat(gameSession.gamePlayers().size()).isEqualTo(2);
        gameSession.gamePlayers().forEach(gamePlayer -> {
            Assertions.assertThat(gamePlayer.id()).isNotNull();
        });

        // gameTurns
        Assertions.assertThat(gameSession.gameTurns().size()).isEqualTo(1);
        gameSession.gameTurns().forEach(gameTurn -> {
            Assertions.assertThat(gameTurn.id()).isNotNull();
        });
    }

    @Test
    @DisplayName("게임 방 ID로 현재 진행중인 게임 세션 조회")
    void 게임_방_ID로_현재_진행중인_게임_세션_조회() throws Exception {
        // given
        User host = userRepository.save(User.create(FingerprintGenerator.generate()));
        User guest = userRepository.save(User.create(FingerprintGenerator.generate()));

        GameRoom gameRoom = GameRoom.create(JoinCodeGenerator.generate(JOIN_CODE_LENGTH), GAME_ROOM_CAPACITY);

        GameRoomParticipant hostParticipant = GameRoomParticipant.create(host.id(), "방장");
        GameRoomParticipant guestParticipant = GameRoomParticipant.create(guest.id(), "유저1");

        gameRoom.letIn(hostParticipant);
        gameRoom.letIn(guestParticipant);

        gameRoomRepository.save(gameRoom);

        gameRoom.changeParticipantReady(guestParticipant, true);

        gameRoom.validateStateToLoad();
        gameRoom.changeStateToPlay();

        GameRoom savedGameRoom = gameRoomRepository.save(gameRoom);


        // when
        GameSession savedGameSession = gameSessionRepository.save(GameSession.create(savedGameRoom.getId(), 2, 20));
        savedGameSession.involvePlayers(
                gameRoom.getCurrentParticipants().stream()
                        .map(gameRoomParticipant ->
                                GamePlayer.create(
                                        savedGameSession.id(),
                                        gameRoomParticipant.getUserId(),
                                        gameRoomParticipant.getNickname()
                                )
                        )
                        .collect(Collectors.toList())
        );
        savedGameSession.start();
        savedGameSession.allocateNewGameTurn("hello world");


        GameSession cascadeSavedGameSession = gameSessionRepository.save(savedGameSession);

        // when
        Optional<GameSession> optionalGameSession = gameSessionRepository.findCurrentGameSessionByGameRoomId(savedGameRoom.getId());

        // then
        Assertions.assertThat(optionalGameSession).isPresent();
        GameSession gameSession = optionalGameSession.get();

        Assertions.assertThat(cascadeSavedGameSession).usingRecursiveComparison().isEqualTo(gameSession);
    }
}

