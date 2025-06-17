package com.bb.webcanvasservice.unit.domain.game.service;

import com.bb.webcanvasservice.common.exception.AbnormalAccessException;
import com.bb.webcanvasservice.common.util.FingerprintGenerator;
import com.bb.webcanvasservice.common.util.JoinCodeGenerator;
import com.bb.webcanvasservice.config.JpaConfig;
import com.bb.webcanvasservice.game.infrastructure.persistence.repository.*;
import com.bb.webcanvasservice.infrastructure.persistence.game.repository.*;
import com.bb.webcanvasservice.game.presentation.request.GameStartRequest;
import com.bb.webcanvasservice.game.infrastructure.persistence.entity.GameRoomJpaEntity;
import com.bb.webcanvasservice.game.infrastructure.persistence.entity.GameRoomEntranceJpaEntity;
import com.bb.webcanvasservice.game.infrastructure.persistence.entity.GameSessionJpaEntity;
import com.bb.webcanvasservice.game.infrastructure.persistence.entity.GameTurnJpaEntity;
import com.bb.webcanvasservice.game.domain.model.GameRoomEntranceRole;
import com.bb.webcanvasservice.game.domain.model.GameRoomState;
import com.bb.webcanvasservice.game.domain.exception.GameSessionIsOverException;
import com.bb.webcanvasservice.domain.game.service.GameRoomFacade;
import com.bb.webcanvasservice.game.domain.service.GameService;
import com.bb.webcanvasservice.user.infrastructure.persistence.entity.UserJpaEntity;
import com.bb.webcanvasservice.user.domain.model.UserState;
import com.bb.webcanvasservice.user.infrastructure.persistence.repository.UserJpaRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.List;

@Transactional
@Import(JpaConfig.class)
@SpringBootTest
@DisplayName("[unit] [service] 게임 서비스 단위테스트")
class GameServiceUnitTest {


    @Autowired
    GameRoomFacade gameRoomFacade;

    @Autowired
    GameSessionJpaRepository gameSessionRepository;

    @Autowired
    UserJpaRepository userJpaRepository;

    @Autowired
    GameRoomJpaRepository gameRoomRepository;

    @Autowired
    GameRoomEntranceJpaRepository gameRoomEntranceRepository;

    @Autowired
    GamePlayHistoryJpaRepository gamePlayHistoryJpaRepository;

    @Autowired
    GameTurnJpaRepository gameTurnRepository;

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
        UserJpaEntity user1 = userJpaRepository.save(new UserJpaEntity(FingerprintGenerator.generate()));
        UserJpaEntity user2 = userJpaRepository.save(new UserJpaEntity(FingerprintGenerator.generate()));
        UserJpaEntity user3 = userJpaRepository.save(new UserJpaEntity(FingerprintGenerator.generate()));

        GameRoomJpaEntity gameRoom = gameRoomRepository.save(new GameRoomJpaEntity(JoinCodeGenerator.generate(6)));

        GameRoomEntranceJpaEntity gameRoomEntrance1 = gameRoomEntranceRepository.save(new GameRoomEntranceJpaEntity(gameRoom, user1, "유저1", GameRoomEntranceRole.HOST));
        gameRoomEntrance1.changeReady(true);

        GameRoomEntranceJpaEntity gameRoomEntrance2 = gameRoomEntranceRepository.save(new GameRoomEntranceJpaEntity(gameRoom, user2, "유저2", GameRoomEntranceRole.GUEST));
        gameRoomEntrance2.changeReady(true);

        GameRoomEntranceJpaEntity gameRoomEntrance3 = gameRoomEntranceRepository.save(new GameRoomEntranceJpaEntity(gameRoom, user3, "유저3", GameRoomEntranceRole.GUEST));
        gameRoomEntrance3.changeReady(true);

        List<GameRoomEntranceJpaEntity> entrances = List.of(gameRoomEntrance1, gameRoomEntrance2, gameRoomEntrance3);

        GameStartRequest gameStartRequest = new GameStartRequest(gameRoom.getId(), 3, 90);


        // when
//        Long  = gameService.startGame(gameStartRequest, user1.getId());
        Assertions.assertThat(gameService.startGame(gameStartRequest, user1.getId())).isNotNull();

        // then

        Assertions.assertThat(gameRoom.getState()).isEqualTo(GameRoomState.PLAYING);
        entrances.stream().forEach(entrance -> {
            Assertions.assertThat(entrance.getUser().getState()).isEqualTo(UserState.IN_GAME);
            Assertions.assertThat(entrance.isReady()).isEqualTo(entrance.getRole() == GameRoomEntranceRole.HOST);
        });
    }


    @Test
    @DisplayName("게임 시작 - HOST가 아닌 유저가 요청보낼 경우 비정상적인 접근 예외 발생")
    void startGameFailedWhenNotHostUserRequestToStartGame() throws Exception {
        // given
        UserJpaEntity user1 = userJpaRepository.save(new UserJpaEntity(FingerprintGenerator.generate()));
        UserJpaEntity user2 = userJpaRepository.save(new UserJpaEntity(FingerprintGenerator.generate()));
        UserJpaEntity user3 = userJpaRepository.save(new UserJpaEntity(FingerprintGenerator.generate()));

        GameRoomJpaEntity gameRoom = gameRoomRepository.save(new GameRoomJpaEntity(JoinCodeGenerator.generate(6)));

        GameRoomEntranceJpaEntity gameRoomEntrance1 = gameRoomEntranceRepository.save(new GameRoomEntranceJpaEntity(gameRoom, user1, "유저1", GameRoomEntranceRole.HOST));
        gameRoomEntrance1.changeReady(true);

        GameRoomEntranceJpaEntity gameRoomEntrance2 = gameRoomEntranceRepository.save(new GameRoomEntranceJpaEntity(gameRoom, user2, "유저2", GameRoomEntranceRole.GUEST));
        gameRoomEntrance2.changeReady(true);

        GameRoomEntranceJpaEntity gameRoomEntrance3 = gameRoomEntranceRepository.save(new GameRoomEntranceJpaEntity(gameRoom, user3, "유저3", GameRoomEntranceRole.GUEST));
        gameRoomEntrance3.changeReady(true);

        List<GameRoomEntranceJpaEntity> entrances = List.of(gameRoomEntrance1, gameRoomEntrance2, gameRoomEntrance3);

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
        UserJpaEntity user1 = userJpaRepository.save(new UserJpaEntity(FingerprintGenerator.generate()));
        UserJpaEntity user2 = userJpaRepository.save(new UserJpaEntity(FingerprintGenerator.generate()));
        UserJpaEntity user3 = userJpaRepository.save(new UserJpaEntity(FingerprintGenerator.generate()));

        GameRoomJpaEntity gameRoom = gameRoomRepository.save(new GameRoomJpaEntity(JoinCodeGenerator.generate(6)));

        GameRoomEntranceJpaEntity gameRoomEntrance1 = gameRoomEntranceRepository.save(new GameRoomEntranceJpaEntity(gameRoom, user1, "유저1", GameRoomEntranceRole.HOST));
        gameRoomEntrance1.changeReady(true);

        GameRoomEntranceJpaEntity gameRoomEntrance2 = gameRoomEntranceRepository.save(new GameRoomEntranceJpaEntity(gameRoom, user2, "유저2", GameRoomEntranceRole.GUEST));
        gameRoomEntrance2.changeReady(true);

        GameRoomEntranceJpaEntity gameRoomEntrance3 = gameRoomEntranceRepository.save(new GameRoomEntranceJpaEntity(gameRoom, user3, "유저3", GameRoomEntranceRole.GUEST));
        gameRoomEntrance3.changeReady(true);

        List<GameRoomEntranceJpaEntity> entrances = List.of(gameRoomEntrance1, gameRoomEntrance2, gameRoomEntrance3);

        GameStartRequest gameStartRequest = new GameStartRequest(gameRoom.getId(), 3, 90);
        Long gameSessionId = gameService.startGame(gameStartRequest, user1.getId());

        gameService.successSubscription(gameSessionId, user1.getId());
        gameService.successSubscription(gameSessionId, user2.getId());
        gameService.successSubscription(gameSessionId, user3.getId());

        // when
        Assertions.assertThat(gameService.findNextDrawerId(gameSessionId)).isNotNull();


        // then
    }

    @Test
    @DisplayName("다음 턴 그림 그릴 사람 조회 - 그림 그린 턴 집계해 골고루 분배 되도록 처리")
    void findNextDrawerBalancedCheck() throws Exception {
        // given
        UserJpaEntity user1 = userJpaRepository.save(new UserJpaEntity(FingerprintGenerator.generate()));
        UserJpaEntity user2 = userJpaRepository.save(new UserJpaEntity(FingerprintGenerator.generate()));
        UserJpaEntity user3 = userJpaRepository.save(new UserJpaEntity(FingerprintGenerator.generate()));

        GameRoomJpaEntity gameRoom = gameRoomRepository.save(new GameRoomJpaEntity(JoinCodeGenerator.generate(6)));

        GameRoomEntranceJpaEntity gameRoomEntrance1 = gameRoomEntranceRepository.save(new GameRoomEntranceJpaEntity(gameRoom, user1, "유저1", GameRoomEntranceRole.HOST));
        gameRoomEntrance1.changeReady(true);

        GameRoomEntranceJpaEntity gameRoomEntrance2 = gameRoomEntranceRepository.save(new GameRoomEntranceJpaEntity(gameRoom, user2, "유저2", GameRoomEntranceRole.GUEST));
        gameRoomEntrance2.changeReady(true);

        GameRoomEntranceJpaEntity gameRoomEntrance3 = gameRoomEntranceRepository.save(new GameRoomEntranceJpaEntity(gameRoom, user3, "유저3", GameRoomEntranceRole.GUEST));
        gameRoomEntrance3.changeReady(true);

        GameStartRequest gameStartRequest = new GameStartRequest(gameRoom.getId(), 5, 90);
        Long gameSessionId = gameService.startGame(gameStartRequest, user1.getId());

        gameService.successSubscription(gameSessionId, user1.getId());
        gameService.successSubscription(gameSessionId, user2.getId());
        gameService.successSubscription(gameSessionId, user3.getId());

        GameSessionJpaEntity gameSession = gameSessionRepository.findById(gameSessionId).get();

        /**
         * 현재 게임세션의 제한 턴을 모두 사용
         */
        gameTurnRepository.save(new GameTurnJpaEntity(gameSession, user1, "랜덤 명사"));
        gameTurnRepository.save(new GameTurnJpaEntity(gameSession, user2, "랜덤 명사2"));
        gameTurnRepository.save(new GameTurnJpaEntity(gameSession, user3, "핸덤 명사3"));
        gameTurnRepository.save(new GameTurnJpaEntity(gameSession, user3, "핸덤 명사123"));


        // when

        /**
         * user3 -> 2회
         * user1, user2 -> 1회
         */
        Long nextDrawer = gameService.findNextDrawerId(gameSessionId);
        Assertions.assertThat(List.of(user1.getId(), user2.getId())).contains(nextDrawer);
    }

    @Test
    @DisplayName("다음 턴 그림 그릴 사람 조회 - 턴 리밋을 넘으면 실패")
    void findNextDrawerFailedWhenTurnLimitOver() throws Exception {
        // given
        UserJpaEntity user1 = userJpaRepository.save(new UserJpaEntity(FingerprintGenerator.generate()));
        UserJpaEntity user2 = userJpaRepository.save(new UserJpaEntity(FingerprintGenerator.generate()));
        UserJpaEntity user3 = userJpaRepository.save(new UserJpaEntity(FingerprintGenerator.generate()));

        GameRoomJpaEntity gameRoom = gameRoomRepository.save(new GameRoomJpaEntity(JoinCodeGenerator.generate(6)));

        GameRoomEntranceJpaEntity gameRoomEntrance1 = gameRoomEntranceRepository.save(new GameRoomEntranceJpaEntity(gameRoom, user1, "유저1", GameRoomEntranceRole.HOST));
        gameRoomEntrance1.changeReady(true);

        GameRoomEntranceJpaEntity gameRoomEntrance2 = gameRoomEntranceRepository.save(new GameRoomEntranceJpaEntity(gameRoom, user2, "유저2", GameRoomEntranceRole.GUEST));
        gameRoomEntrance2.changeReady(true);

        GameRoomEntranceJpaEntity gameRoomEntrance3 = gameRoomEntranceRepository.save(new GameRoomEntranceJpaEntity(gameRoom, user3, "유저3", GameRoomEntranceRole.GUEST));
        gameRoomEntrance3.changeReady(true);

        List<GameRoomEntranceJpaEntity> entrances = List.of(gameRoomEntrance1, gameRoomEntrance2, gameRoomEntrance3);

        GameStartRequest gameStartRequest = new GameStartRequest(gameRoom.getId(), 3, 90);
        Long gameSessionId = gameService.startGame(gameStartRequest, user1.getId());

        GameSessionJpaEntity gameSession = gameSessionRepository.findById(gameSessionId).get();

        /**
         * 현재 게임세션의 제한 턴을 모두 사용
         */
        gameTurnRepository.save(new GameTurnJpaEntity(gameSession, user1, "랜덤 명사"));
        gameTurnRepository.save(new GameTurnJpaEntity(gameSession, user2, "랜덤 명사2"));
        gameTurnRepository.save(new GameTurnJpaEntity(gameSession, user3, "핸덤 명사3"));

        // when
        Assertions.assertThatThrownBy(() -> gameService.findNextDrawerId(gameSessionId))
                        .isInstanceOf(GameSessionIsOverException.class);

        // then
    }

}