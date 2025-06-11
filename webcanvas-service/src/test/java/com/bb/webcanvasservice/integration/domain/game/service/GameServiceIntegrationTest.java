package com.bb.webcanvasservice.integration.domain.game.service;

import com.bb.webcanvasservice.common.exception.AbnormalAccessException;
import com.bb.webcanvasservice.common.util.FingerprintGenerator;
import com.bb.webcanvasservice.domain.dictionary.service.DictionaryService;
import com.bb.webcanvasservice.domain.game.dto.request.GameStartRequest;
import com.bb.webcanvasservice.domain.game.dto.response.GameRoomEntranceResponse;
import com.bb.webcanvasservice.domain.game.entity.GameRoomEntrance;
import com.bb.webcanvasservice.domain.game.entity.GameSession;
import com.bb.webcanvasservice.domain.game.enums.GameRoomEntranceState;
import com.bb.webcanvasservice.domain.game.enums.GameSessionState;
import com.bb.webcanvasservice.domain.game.repository.GameRoomEntranceRepository;
import com.bb.webcanvasservice.domain.game.service.GameRoomFacade;
import com.bb.webcanvasservice.domain.game.service.GameService;
import com.bb.webcanvasservice.domain.user.entity.User;
import com.bb.webcanvasservice.domain.user.model.UserStateCode;
import com.bb.webcanvasservice.infrastructure.persistence.user.UserJpaRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static com.bb.webcanvasservice.domain.game.enums.GameRoomRole.GUEST;
import static org.mockito.ArgumentMatchers.any;

@Transactional
@SpringBootTest
@DisplayName("[integration] [service] 게임 서비스 통합테스트")
class GameServiceIntegrationTest {


    @Autowired
    GameService gameService;

    @Autowired
    GameRoomFacade gameRoomFacade;

    @Autowired
    UserJpaRepository userJpaRepository;

    @Autowired
    GameRoomEntranceRepository gameRoomEntranceRepository;

    @MockitoBean
    DictionaryService dictionaryService;

    @Test
    @DisplayName("게임 방 퇴장 - 퇴장시 유저 상태가 IN_LOBBY로 변경된다.")
    void exitFromGameRoom() throws Exception {
        // given
        BDDMockito.given(dictionaryService.drawRandomWordValue(any(), any()))
                .willReturn("테스트 명사");

        User user1 = userJpaRepository.save(new User(FingerprintGenerator.generate()));
        User user2 = userJpaRepository.save(new User(FingerprintGenerator.generate()));

        GameRoomEntranceResponse user1Entrance = gameRoomFacade.createGameRoomAndEnter(user1.getId());
        GameRoomEntranceResponse user2Entrance = gameRoomFacade.enterGameRoom(user1Entrance.gameRoomId(), user2.getId(), GUEST);

        // when
        gameRoomFacade.exitFromRoom(user2Entrance.gameRoomEntranceId(), user2.getId());

        // then
        Assertions.assertThat(user2.getState()).isEqualTo(UserStateCode.IN_LOBBY);
    }

    @Test
    @DisplayName("게임 시작")
    void startGame() throws Exception {
        // given
        User user1 = userJpaRepository.save(new User(FingerprintGenerator.generate()));
        User user2 = userJpaRepository.save(new User(FingerprintGenerator.generate()));
        User user3 = userJpaRepository.save(new User(FingerprintGenerator.generate()));

        GameRoomEntranceResponse user1Entrance = gameRoomFacade.createGameRoomAndEnter(user1.getId());
        Long gameRoomId = user1Entrance.gameRoomId();

        GameRoomEntranceResponse user2Entrance = gameRoomFacade.enterGameRoom(gameRoomId, user2.getId(), GUEST);
        GameRoomEntranceResponse user3Entrance = gameRoomFacade.enterGameRoom(gameRoomId, user3.getId(), GUEST);

        // 레디
        gameRoomFacade.updateReady(user2Entrance.gameRoomEntranceId(), user2.getId(), true);
        gameRoomFacade.updateReady(user3Entrance.gameRoomEntranceId(), user3.getId(), true);

        // when
        GameStartRequest gameStartRequest = new GameStartRequest(gameRoomId, 3, 70);

        /**
         * 방장이 아닌 유저가 게임시작 요청시 Exception 발생
         */
        Assertions.assertThatThrownBy(() -> gameService.startGame(gameStartRequest, user2.getId()))
                .isInstanceOf(AbnormalAccessException.class);

        Long gameSessionId = gameService.startGame(gameStartRequest, user1.getId());
        // then
        Assertions.assertThat(gameSessionId).isNotNull();
        Assertions.assertThat(userJpaRepository.findUserState(user1.getId())).isEqualTo(UserStateCode.IN_GAME);
        Assertions.assertThat(userJpaRepository.findUserState(user2.getId())).isEqualTo(UserStateCode.IN_GAME);
        Assertions.assertThat(userJpaRepository.findUserState(user3.getId())).isEqualTo(UserStateCode.IN_GAME);
    }

    @Test
    @DisplayName("게임 종료")
    void endGame() throws Exception {
        // given
        // given
        User user1 = userJpaRepository.save(new User(FingerprintGenerator.generate()));
        User user2 = userJpaRepository.save(new User(FingerprintGenerator.generate()));
        User user3 = userJpaRepository.save(new User(FingerprintGenerator.generate()));

        GameRoomEntranceResponse user1Entrance = gameRoomFacade.createGameRoomAndEnter(user1.getId());
        Long gameRoomId = user1Entrance.gameRoomId();

        GameRoomEntranceResponse user2Entrance = gameRoomFacade.enterGameRoom(gameRoomId, user2.getId(), GUEST);
        GameRoomEntranceResponse user3Entrance = gameRoomFacade.enterGameRoom(gameRoomId, user3.getId(), GUEST);

        // 레디
        gameRoomFacade.updateReady(user2Entrance.gameRoomEntranceId(), user2.getId(), true);
        gameRoomFacade.updateReady(user3Entrance.gameRoomEntranceId(), user3.getId(), true);

        // when
        GameStartRequest gameStartRequest = new GameStartRequest(gameRoomId, 3, 70);
        Long gameSessionId = gameService.startGame(gameStartRequest, user1.getId());
        gameService.successSubscription(gameSessionId, user1.getId());
        gameService.successSubscription(gameSessionId, user2.getId());
        gameService.successSubscription(gameSessionId, user3.getId());

        gameService.endGame(gameSessionId);

        // then
        Assertions.assertThat(user1.getState()).isEqualTo(UserStateCode.IN_ROOM);
        Assertions.assertThat(user2.getState()).isEqualTo(UserStateCode.IN_ROOM);
        Assertions.assertThat(user3.getState()).isEqualTo(UserStateCode.IN_ROOM);

        GameRoomEntrance user1EntranceEntity = gameRoomEntranceRepository.findById(user1Entrance.gameRoomEntranceId()).get();
        Assertions.assertThat(user1EntranceEntity.getState()).isEqualTo(GameRoomEntranceState.WAITING);

        GameRoomEntrance user2EntranceEntity = gameRoomEntranceRepository.findById(user2Entrance.gameRoomEntranceId()).get();
        Assertions.assertThat(user2EntranceEntity.getState()).isEqualTo(GameRoomEntranceState.WAITING);

        GameRoomEntrance user3EntranceEntity = gameRoomEntranceRepository.findById(user3Entrance.gameRoomEntranceId()).get();
        Assertions.assertThat(user3EntranceEntity.getState()).isEqualTo(GameRoomEntranceState.WAITING);

        GameSession gameSession = gameService.findGameSession(gameSessionId);
        Assertions.assertThat(gameSession.getState()).isEqualTo(GameSessionState.COMPLETED);
    }
}