package com.bb.webcanvasservice.small.game.domain.model.session;

import com.bb.webcanvasservice.game.domain.model.session.GameSession;
import com.bb.webcanvasservice.game.domain.model.session.GameSessionState;
import com.bb.webcanvasservice.game.domain.model.session.GameTurn;
import com.bb.webcanvasservice.game.domain.model.session.GameTurnState;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Tag("small")
@DisplayName("[small] [game.session] [domain] 게임 세션 도메인 모델 테스트")
public class GameSessionTest {

    @Test
    @DisplayName("")
    void 게임_종료_수행_여부_테스트() throws Exception {
        // given
        long gameTurnIdSeq = 0L;
        long gameSessionId = 999L;

        long drawerId1 = 1L;
        long drawerId2 = 2L;
        long drawerId3 = 3L;
        long drawerId4 = 4L;
        int duration = 10;
        GameTurn gameTurn1 = new GameTurn(++gameTurnIdSeq, gameSessionId, drawerId1, "", LocalDateTime.now(), 22L, GameTurnState.ANSWERED, duration);
        GameTurn gameTurn2 = new GameTurn(++gameTurnIdSeq, gameSessionId, drawerId2, "", LocalDateTime.now(), null, GameTurnState.PASSED, duration);
        GameTurn gameTurn3 = new GameTurn(++gameTurnIdSeq, gameSessionId, drawerId3, "", LocalDateTime.now(), 23L, GameTurnState.ANSWERED, duration);
        GameTurn gameTurn4 = new GameTurn(++gameTurnIdSeq, gameSessionId, drawerId4, "", LocalDateTime.now(), 22L, GameTurnState.ANSWERED, duration);

        List<GameTurn> gameTurns = List.of(gameTurn1, gameTurn2, gameTurn3, gameTurn4);

        GameSession gameSession = new GameSession(gameSessionId, 12L, 4, 10, GameSessionState.PLAYING, new ArrayList<>(), gameTurns);

        // when
        Assertions.assertThat(gameSession.shouldEnd()).isTrue();

        // then

    }
}
