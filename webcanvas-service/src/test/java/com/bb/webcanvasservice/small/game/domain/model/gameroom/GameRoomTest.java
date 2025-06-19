package com.bb.webcanvasservice.small.game.domain.model.gameroom;

import com.bb.webcanvasservice.common.util.JoinCodeGenerator;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoom;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameSession;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("small")
@DisplayName("[small] [game] 게임 방 도메인 모델 테스트")
class GameRoomTest {

    @Test
    @DisplayName("게임 방을 로드한다.")
    void testGetCurrentGameSession() throws Exception {
        // given
        GameRoom gameRoom = GameRoom.create(JoinCodeGenerator.generate(6), 8);
        gameRoom.loadGameSession(150);

        // when
        GameSession gameSession = gameRoom.getCurrentGameSession();

        // then
        Assertions.assertThat(gameSession.getTimePerTurn()).isEqualTo(150);
        Assertions.assertThat(gameSession.isLoading()).isTrue();
    }

}