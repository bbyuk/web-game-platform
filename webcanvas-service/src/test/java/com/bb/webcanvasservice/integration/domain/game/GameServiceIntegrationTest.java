package com.bb.webcanvasservice.integration.domain.game;

import com.bb.webcanvasservice.common.RandomCodeGenerator;
import com.bb.webcanvasservice.domain.game.GameRoom;
import com.bb.webcanvasservice.domain.game.GameRoomEntrance;
import com.bb.webcanvasservice.domain.game.GameService;
import com.bb.webcanvasservice.domain.game.enums.GameRoomState;
import com.bb.webcanvasservice.domain.game.exception.AlreadyEnteredRoomException;
import com.bb.webcanvasservice.domain.game.exception.IllegalGameRoomStateException;
import com.bb.webcanvasservice.domain.game.repository.GameRoomEntranceRepository;
import com.bb.webcanvasservice.domain.game.repository.GameRoomRepository;
import com.bb.webcanvasservice.domain.user.User;
import com.bb.webcanvasservice.domain.user.UserRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class GameServiceIntegrationTest {

    @Autowired
    private GameService gameService;

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
        Long enterGameRoomId = gameService.enterGameRoom(waitingRoom.getId(), testUser.getId());

        // then
        Assertions.assertThat(enterGameRoomId).isNotNull();
    }

    @Test
    @DisplayName("게임 방 입장 - 상태가 PLAYING인 방에 입장 요청시 실패")
    public void enterGameRoomFailWhenTargetGameRoomStateIsPlaying() {
        // given - 공통 Entity 사용

        // when
        Assertions.assertThatThrownBy(() -> gameService.enterGameRoom(playingRoom.getId(), testUser.getId()))
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
        Assertions.assertThatThrownBy(() -> gameService.enterGameRoom(waitingRoom.getId(), testUser.getId()))
                .isInstanceOf(AlreadyEnteredRoomException.class);

        // then
    }

    @Test
    @DisplayName("게임 방 생성")
    public void createGameRoom() {
        // given

        // when

        // then
    }
}