package com.bb.webcanvasservice.unit.domain.game.service;

import com.bb.webcanvasservice.common.FingerprintGenerator;
import com.bb.webcanvasservice.common.JoinCodeGenerator;
import com.bb.webcanvasservice.domain.game.dto.request.GameStartRequest;
import com.bb.webcanvasservice.domain.game.entity.GameRoom;
import com.bb.webcanvasservice.domain.game.entity.GameRoomEntrance;
import com.bb.webcanvasservice.domain.game.enums.GameRoomRole;
import com.bb.webcanvasservice.domain.game.enums.GameRoomState;
import com.bb.webcanvasservice.domain.game.repository.GamePlayHistoryRepository;
import com.bb.webcanvasservice.domain.game.repository.GameRoomEntranceRepository;
import com.bb.webcanvasservice.domain.game.repository.GameRoomRepository;
import com.bb.webcanvasservice.domain.game.repository.GameSessionRepository;
import com.bb.webcanvasservice.domain.game.service.GameRoomService;
import com.bb.webcanvasservice.domain.game.service.GameService;
import com.bb.webcanvasservice.domain.user.User;
import com.bb.webcanvasservice.domain.user.UserRepository;
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

        GameRoomEntrance gameRoomEntrance2 = gameRoomEntranceRepository.save(new GameRoomEntrance(gameRoom, user2, "유저2", GameRoomRole.HOST));
        gameRoomEntrance2.changeReady(true);

        GameRoomEntrance gameRoomEntrance3 = gameRoomEntranceRepository.save(new GameRoomEntrance(gameRoom, user3, "유저3", GameRoomRole.HOST));
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


}