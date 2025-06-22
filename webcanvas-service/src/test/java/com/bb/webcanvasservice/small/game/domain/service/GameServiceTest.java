package com.bb.webcanvasservice.small.game.domain.service;

import com.bb.webcanvasservice.game.application.command.JoinGameRoomCommand;
import com.bb.webcanvasservice.game.application.command.StartGameCommand;
import com.bb.webcanvasservice.game.application.command.UpdateReadyCommand;
import com.bb.webcanvasservice.game.application.config.GameProperties;
import com.bb.webcanvasservice.game.application.dto.GameRoomJoinDetailInfoDto;
import com.bb.webcanvasservice.game.application.dto.GameRoomJoinDto;
import com.bb.webcanvasservice.game.application.dto.GameRoomListDto;
import com.bb.webcanvasservice.game.application.dto.JoinedUserInfoDto;
import com.bb.webcanvasservice.game.application.service.GameService;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoomParticipantRole;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoomState;
import com.bb.webcanvasservice.small.game.dummy.ApplicationEventPublisherDummy;
import com.bb.webcanvasservice.small.game.stub.service.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

@Tag("small")
@DisplayName("[small] [service] GameService small test")
public class GameServiceTest {

    /**
     * Stub으로 셋팅한 GameService
     */
    private final int gameRoomCapacity = 6;

    GameService gameService = new GameService(
            new GameDictionaryQueryPortStub(),
            new GameUserCommandPortStub(),
            new GameGameRoomRepositoryStub(),
            new GameGamePlayHistoryRepositoryStub(),
            new GameGameSessionLoadRegistryStub(),
            new GameProperties(gameRoomCapacity,
                    5,
                    8,
                    List.of(
                            "#ff3c00",
                            "#0042ff",
                            "#1e9000",
                            "#f2cb00",
                            "#8400a8",
                            "#00c8c8",
                            "#ff68ff",
                            "#969696"
                    ),
                    new ArrayList<>()
            ),
            new ApplicationEventPublisherDummy()
    );

    @Test
    @DisplayName("게임 방 생성 및 입장 - 성공 테스트")
    void testCreateGameRoomAndEnter() throws Exception {
        // given
        Long userId = 23L;

        // when
        GameRoomJoinDto resultDto = gameService.createGameRoomAndEnter(userId);

        // then
        Assertions.assertThat(resultDto.gameRoomId()).isEqualTo(1L);
        Assertions.assertThat(resultDto.gameRoomParticipantId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("현재 입장한 게임 방 및 입장 정보 조회 - 성공 테스트")
    void testFindJoinedGameRoomInfo() throws Exception {
        // given
        Long userId = 1L;

        GameRoomJoinDto gameRoomAndEnter = gameService.createGameRoomAndEnter(userId);

        // when
        GameRoomJoinDetailInfoDto joinedGameRoomInfo = gameService.findJoinedGameRoomInfo(userId);

        JoinedUserInfoDto playerJoinInfo = new JoinedUserInfoDto(
                userId,
                "#ff3c00",
                "깔끔한 플레이어",
                GameRoomParticipantRole.HOST,
                true
        );
        GameRoomJoinDetailInfoDto resultDto = new GameRoomJoinDetailInfoDto(
                gameRoomAndEnter.gameRoomId(),
                gameRoomAndEnter.gameRoomParticipantId(),
                List.of(playerJoinInfo),
                GameRoomState.WAITING,
                playerJoinInfo
        );

        // then
        Assertions.assertThat(joinedGameRoomInfo).usingRecursiveComparison().isEqualTo(resultDto);
    }

    @Test
    @DisplayName("입장 가능한 게임 방 조회 - 성공 테스트")
    void testFindJoinableGameRooms() throws Exception {
        // given
        Long userId1 = 1L;
        Long userId2 = 2L;
        Long userId3 = 3L;
        Long userId4 = 4L;

        Long userId5 = 5L;

        GameRoomJoinDto gameRoomJoinDto1 = gameService.createGameRoomAndEnter(userId1);
        GameRoomJoinDto gameRoomJoinDto2 = gameService.createGameRoomAndEnter(userId2);
        GameRoomJoinDto gameRoomJoinDto3 = gameService.createGameRoomAndEnter(userId3);
        GameRoomJoinDto gameRoomJoinDto4 = gameService.createGameRoomAndEnter(userId4);


        // when
        GameRoomListDto joinableGameRooms = gameService.findJoinableGameRooms(userId5);

        // then
        Assertions.assertThat(joinableGameRooms.roomList().size()).isEqualTo(4);
    }

    @Test
    @DisplayName("입장 가능한 게임 방 조회 - 게임 방 정원이 꽉찬 게임 방은 조회되지 않는다.")
    void testFindJoinableGameRooms_1() throws Exception {
        // given
        Long userIdSeq = 0L;

        GameRoomJoinDto gameRoomJoinDto1 = gameService.createGameRoomAndEnter(++userIdSeq);
        GameRoomJoinDto gameRoomJoinDto2 = gameService.createGameRoomAndEnter(++userIdSeq);
        GameRoomJoinDto gameRoomJoinDto3 = gameService.createGameRoomAndEnter(++userIdSeq);
        GameRoomJoinDto gameRoomJoinDto4 = gameService.createGameRoomAndEnter(++userIdSeq);

        // when
        for (int i = 1; i < gameRoomCapacity; i++) {
            gameService.joinGameRoom(new JoinGameRoomCommand(gameRoomJoinDto1.gameRoomId(), ++userIdSeq));
        }
        GameRoomListDto joinableGameRooms = gameService.findJoinableGameRooms(++userIdSeq);

        // then
        Assertions.assertThat(joinableGameRooms.roomList().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("입장 가능한 게임 방 조회 - 게임방 상태가 WAITING인 게임 방만 조회된다.")
    void testFindJoinableGameRooms_2() throws Exception {
        // given
        Long userIdSeq = 0L;
        Long gameRoom1HostId = ++userIdSeq;
        GameRoomJoinDto gameRoomJoinDto1 = gameService.createGameRoomAndEnter(gameRoom1HostId);
        GameRoomJoinDto gameRoomJoinDto2 = gameService.createGameRoomAndEnter(++userIdSeq);
        GameRoomJoinDto gameRoomJoinDto3 = gameService.createGameRoomAndEnter(++userIdSeq);
        GameRoomJoinDto gameRoomJoinDto4 = gameService.createGameRoomAndEnter(++userIdSeq);

        // when
        Long gameRoom1GuestId = ++userIdSeq;
        GameRoomJoinDto gameRoomJoinDto = gameService.joinGameRoom(new JoinGameRoomCommand(gameRoomJoinDto1.gameRoomId(), gameRoom1GuestId));
        gameService.updateReady(new UpdateReadyCommand(gameRoomJoinDto.gameRoomParticipantId(), gameRoom1GuestId, true));
        gameService.startGame(new StartGameCommand(gameRoomJoinDto1.gameRoomId(), 2, 20, gameRoom1HostId));

        GameRoomListDto joinableGameRooms = gameService.findJoinableGameRooms(++userIdSeq);
        // then

        Assertions.assertThat(joinableGameRooms.roomList().size()).isEqualTo(3);

    }
}
