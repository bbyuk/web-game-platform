package com.bb.webcanvasservice.small.game.domain.service;

import com.bb.webcanvasservice.game.application.config.GameProperties;
import com.bb.webcanvasservice.game.application.dto.GameRoomJoinDto;
import com.bb.webcanvasservice.game.application.service.GameService;
import com.bb.webcanvasservice.small.game.dummy.ApplicationEventPublisherDummy;
import com.bb.webcanvasservice.small.game.stub.service.*;
import com.bb.webcanvasservice.user.domain.model.User;
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
    GameService gameService = new GameService(
            new GameDictionaryQueryPortStub(),
            new GameUserCommandPortStub(),
            new GameGameRoomRepositoryStub(),
            new GameGamePlayHistoryRepositoryStub(),
            new GameGameSessionLoadRegistryStub(),
            new GameProperties(6,
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
}
