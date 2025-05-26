package com.bb.webcanvasservice.unit.domain.game.service;

import com.bb.webcanvasservice.common.FingerprintGenerator;
import com.bb.webcanvasservice.common.JoinCodeGenerator;
import com.bb.webcanvasservice.common.exception.AbnormalAccessException;
import com.bb.webcanvasservice.domain.game.dto.request.GameStartRequest;
import com.bb.webcanvasservice.domain.game.entity.GameRoom;
import com.bb.webcanvasservice.domain.game.entity.GameRoomEntrance;
import com.bb.webcanvasservice.domain.game.entity.GameSession;
import com.bb.webcanvasservice.domain.game.entity.GameTurn;
import com.bb.webcanvasservice.domain.game.enums.GameRoomRole;
import com.bb.webcanvasservice.domain.game.enums.GameRoomState;
import com.bb.webcanvasservice.domain.game.exception.GameSessionIsOverException;
import com.bb.webcanvasservice.domain.game.repository.*;
import com.bb.webcanvasservice.domain.game.service.GameRoomService;
import com.bb.webcanvasservice.domain.game.service.GameService;
import com.bb.webcanvasservice.domain.user.User;
import com.bb.webcanvasservice.domain.user.UserRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.List;

import static com.bb.webcanvasservice.domain.game.enums.GameRoomEntranceState.PLAYING;

@Transactional
@SpringBootTest
@DisplayName("[unit] [service] 게임 서비스 단위테스트")
class GameServiceUnitTest {


    @Autowired
    GameRoomService gameRoomService;

    @Autowired
    GameSessionRepository gameSessionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    GameRoomRepository gameRoomRepository;

    @Autowired
    GameRoomEntranceRepository gameRoomEntranceRepository;

    @Autowired
    GamePlayHistoryRepository gamePlayHistoryRepository;

    @Autowired
    GameTurnRepository gameTurnRepository;

    @Autowired
    EntityManager em;

    @Autowired
    GameService gameService;

    private void setId(Object object, Long id) throws Exception {
        Field field = object.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(object, id);
        field.setAccessible(false);
    }

    @Test
    @DisplayName("게임 시작 - 게임 시작 테스트")
    void testStartGame() throws Exception {
        // given
        User user1 = userRepository.save(new User(FingerprintGenerator.generate()));
        User user2 = userRepository.save(new User(FingerprintGenerator.generate()));
        User user3 = userRepository.save(new User(FingerprintGenerator.generate()));

        GameRoom gameRoom = gameRoomRepository.save(new GameRoom(JoinCodeGenerator.generate(6)));

        GameRoomEntrance gameRoomEntrance1 = gameRoomEntranceRepository.save(new GameRoomEntrance(gameRoom, user1, "유저1", GameRoomRole.HOST));
        gameRoomEntrance1.changeReady(true);

        GameRoomEntrance gameRoomEntrance2 = gameRoomEntranceRepository.save(new GameRoomEntrance(gameRoom, user2, "유저2", GameRoomRole.GUEST));
        gameRoomEntrance2.changeReady(true);

        GameRoomEntrance gameRoomEntrance3 = gameRoomEntranceRepository.save(new GameRoomEntrance(gameRoom, user3, "유저3", GameRoomRole.GUEST));
        gameRoomEntrance3.changeReady(true);

        List<GameRoomEntrance> entrances = List.of(gameRoomEntrance1, gameRoomEntrance2, gameRoomEntrance3);

        GameStartRequest gameStartRequest = new GameStartRequest(gameRoom.getId(), 3, 90);


        // when
//        Long  = gameService.startGame(gameStartRequest, user1.getId());
        Assertions.assertThat(gameService.startGame(gameStartRequest, user1.getId())).isNotNull();

        // then
        Assertions.assertThat(gameRoom.getState()).isEqualTo(GameRoomState.PLAYING);
        entrances.stream().forEach(entrance -> Assertions.assertThat(entrance.getState()).isEqualTo(PLAYING));
    }


    @Test
    @DisplayName("게임 시작 - HOST가 아닌 유저가 요청보낼 경우 비정상적인 접근 예외 발생")
    void startGameFailedWhenNotHostUserRequestToStartGame() throws Exception {
        // given
        User user1 = userRepository.save(new User(FingerprintGenerator.generate()));
        User user2 = userRepository.save(new User(FingerprintGenerator.generate()));
        User user3 = userRepository.save(new User(FingerprintGenerator.generate()));

        GameRoom gameRoom = gameRoomRepository.save(new GameRoom(JoinCodeGenerator.generate(6)));

        GameRoomEntrance gameRoomEntrance1 = gameRoomEntranceRepository.save(new GameRoomEntrance(gameRoom, user1, "유저1", GameRoomRole.HOST));
        gameRoomEntrance1.changeReady(true);

        GameRoomEntrance gameRoomEntrance2 = gameRoomEntranceRepository.save(new GameRoomEntrance(gameRoom, user2, "유저2", GameRoomRole.GUEST));
        gameRoomEntrance2.changeReady(true);

        GameRoomEntrance gameRoomEntrance3 = gameRoomEntranceRepository.save(new GameRoomEntrance(gameRoom, user3, "유저3", GameRoomRole.GUEST));
        gameRoomEntrance3.changeReady(true);

        List<GameRoomEntrance> entrances = List.of(gameRoomEntrance1, gameRoomEntrance2, gameRoomEntrance3);

        GameStartRequest gameStartRequest = new GameStartRequest(gameRoom.getId(), 3, 90);

        // when
        Assertions.assertThatThrownBy(() -> gameService.startGame(gameStartRequest, user2.getId()))
                .isInstanceOf(AbnormalAccessException.class);

        // then
    }


    @Test
    @DisplayName("다음 턴 그림그릴 사람 조회 - 다음 그림 그릴 사람을 랜덤하게 골라 조회한다.")
    void findNextDrawer() throws Exception {
        // given
        // given
        User user1 = userRepository.save(new User(FingerprintGenerator.generate()));
        User user2 = userRepository.save(new User(FingerprintGenerator.generate()));
        User user3 = userRepository.save(new User(FingerprintGenerator.generate()));

        GameRoom gameRoom = gameRoomRepository.save(new GameRoom(JoinCodeGenerator.generate(6)));

        GameRoomEntrance gameRoomEntrance1 = gameRoomEntranceRepository.save(new GameRoomEntrance(gameRoom, user1, "유저1", GameRoomRole.HOST));
        gameRoomEntrance1.changeReady(true);

        GameRoomEntrance gameRoomEntrance2 = gameRoomEntranceRepository.save(new GameRoomEntrance(gameRoom, user2, "유저2", GameRoomRole.GUEST));
        gameRoomEntrance2.changeReady(true);

        GameRoomEntrance gameRoomEntrance3 = gameRoomEntranceRepository.save(new GameRoomEntrance(gameRoom, user3, "유저3", GameRoomRole.GUEST));
        gameRoomEntrance3.changeReady(true);

        List<GameRoomEntrance> entrances = List.of(gameRoomEntrance1, gameRoomEntrance2, gameRoomEntrance3);

        GameStartRequest gameStartRequest = new GameStartRequest(gameRoom.getId(), 3, 90);
        Long gameSessionId = gameService.startGame(gameStartRequest, user1.getId());

        // when
        Assertions.assertThat(gameService.findNextDrawer(gameSessionId)).isNotNull();


        // then
    }

    @Test
    @DisplayName("다음 턴 그림 그릴 사람 조회 - 그림 그린 턴 집계해 골고루 분배 되도록 처리")
    void findNextDrawerBalancedCheck() throws Exception {
        // given
        User user1 = userRepository.save(new User(FingerprintGenerator.generate()));
        User user2 = userRepository.save(new User(FingerprintGenerator.generate()));
        User user3 = userRepository.save(new User(FingerprintGenerator.generate()));

        GameRoom gameRoom = gameRoomRepository.save(new GameRoom(JoinCodeGenerator.generate(6)));

        GameRoomEntrance gameRoomEntrance1 = gameRoomEntranceRepository.save(new GameRoomEntrance(gameRoom, user1, "유저1", GameRoomRole.HOST));
        gameRoomEntrance1.changeReady(true);

        GameRoomEntrance gameRoomEntrance2 = gameRoomEntranceRepository.save(new GameRoomEntrance(gameRoom, user2, "유저2", GameRoomRole.GUEST));
        gameRoomEntrance2.changeReady(true);

        GameRoomEntrance gameRoomEntrance3 = gameRoomEntranceRepository.save(new GameRoomEntrance(gameRoom, user3, "유저3", GameRoomRole.GUEST));
        gameRoomEntrance3.changeReady(true);

        List<GameRoomEntrance> entrances = List.of(gameRoomEntrance1, gameRoomEntrance2, gameRoomEntrance3);

        GameStartRequest gameStartRequest = new GameStartRequest(gameRoom.getId(), 5, 90);
        Long gameSessionId = gameService.startGame(gameStartRequest, user1.getId());

        GameSession gameSession = gameSessionRepository.findById(gameSessionId).get();

        /**
         * 현재 게임세션의 제한 턴을 모두 사용
         */
        gameTurnRepository.save(new GameTurn(gameSession, user1, "랜덤 명사"));
        gameTurnRepository.save(new GameTurn(gameSession, user2, "랜덤 명사2"));
        gameTurnRepository.save(new GameTurn(gameSession, user3, "핸덤 명사3"));
        gameTurnRepository.save(new GameTurn(gameSession, user3, "핸덤 명사123"));


        // when

        /**
         * 3L -> 2회
         * 1L, 2L -> 1회
         */
        Long nextDrawer = gameService.findNextDrawer(gameSessionId);
        Assertions.assertThat(List.of(1L, 2L)).contains(nextDrawer);
    }

    @Test
    @DisplayName("다음 턴 그림 그릴 사람 조회 - 턴 리밋을 넘으면 실패")
    void findNextDrawerFailedWhenTurnLimitOver() throws Exception {
        // given
        User user1 = userRepository.save(new User(FingerprintGenerator.generate()));
        User user2 = userRepository.save(new User(FingerprintGenerator.generate()));
        User user3 = userRepository.save(new User(FingerprintGenerator.generate()));

        GameRoom gameRoom = gameRoomRepository.save(new GameRoom(JoinCodeGenerator.generate(6)));

        GameRoomEntrance gameRoomEntrance1 = gameRoomEntranceRepository.save(new GameRoomEntrance(gameRoom, user1, "유저1", GameRoomRole.HOST));
        gameRoomEntrance1.changeReady(true);

        GameRoomEntrance gameRoomEntrance2 = gameRoomEntranceRepository.save(new GameRoomEntrance(gameRoom, user2, "유저2", GameRoomRole.GUEST));
        gameRoomEntrance2.changeReady(true);

        GameRoomEntrance gameRoomEntrance3 = gameRoomEntranceRepository.save(new GameRoomEntrance(gameRoom, user3, "유저3", GameRoomRole.GUEST));
        gameRoomEntrance3.changeReady(true);

        List<GameRoomEntrance> entrances = List.of(gameRoomEntrance1, gameRoomEntrance2, gameRoomEntrance3);

        GameStartRequest gameStartRequest = new GameStartRequest(gameRoom.getId(), 3, 90);
        Long gameSessionId = gameService.startGame(gameStartRequest, user1.getId());

        GameSession gameSession = gameSessionRepository.findById(gameSessionId).get();

        /**
         * 현재 게임세션의 제한 턴을 모두 사용
         */
        gameTurnRepository.save(new GameTurn(gameSession, user1, "랜덤 명사"));
        gameTurnRepository.save(new GameTurn(gameSession, user2, "랜덤 명사2"));
        gameTurnRepository.save(new GameTurn(gameSession, user3, "핸덤 명사3"));

        // when
        Assertions.assertThatThrownBy(() -> gameService.findNextDrawer(gameSessionId))
                        .isInstanceOf(GameSessionIsOverException.class);

        // then
    }
}