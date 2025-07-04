package com.bb.webcanvasservice.small.game.domain.model.session;

import com.bb.webcanvasservice.game.domain.model.session.GameTurn;
import com.bb.webcanvasservice.game.domain.model.session.GameTurnState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

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
//        new GameTurn(++gameTurnIdSeq, gameSessionId, drawerId1, "", LocalDateTime.now(), 22L, GameTurnState.ANSWERED);

        // when

        // then

    }
}
