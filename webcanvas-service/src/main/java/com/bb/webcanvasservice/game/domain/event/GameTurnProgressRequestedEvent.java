package com.bb.webcanvasservice.game.domain.event;

import com.bb.webcanvasservice.domain.shared.event.ApplicationEvent;
import lombok.ToString;

/**
 * 게임 턴 진행처리 요청 이벤트
 * pass or answered 시
 */

@ToString
public class GameTurnProgressRequestedEvent extends ApplicationEvent {

    private final Long gameRoomId;
    private final Long gameSessionId;
    private final Long gameTurnId;
    private final int gameTurnPeriod;
    private final Long answererId;
    private final int nextTurnStartCountdownDelaySeconds;

    public GameTurnProgressRequestedEvent(Long gameRoomId, Long gameSessionId, Long gameTurnId, int gameTurnPeriod, Long answererId, int nextTurnStartCountdownDelaySeconds) {
        super("SESSION/TURN_PROGRESS_REQUESTED");
        this.gameRoomId = gameRoomId;
        this.gameSessionId = gameSessionId;
        this.gameTurnId = gameTurnId;
        this.gameTurnPeriod = gameTurnPeriod;
        this.answererId = answererId;
        this.nextTurnStartCountdownDelaySeconds = nextTurnStartCountdownDelaySeconds;
    }

    public GameTurnProgressRequestedEvent(Long gameRoomId, Long gameSessionId, int gameTurnPeriod, Long gameTurnId, int nextTurnStartCountdownDelaySeconds) {
        this(gameRoomId, gameSessionId, gameTurnId, gameTurnPeriod, null, nextTurnStartCountdownDelaySeconds);
    }

    public Long getGameRoomId() {
        return gameRoomId;
    }

    public Long getGameSessionId() {
        return gameSessionId;
    }

    public Long getGameTurnId() {
        return gameTurnId;
    }

    public int getGameTurnPeriod() {
        return gameTurnPeriod;
    }

    public Long getAnswererId() {
        return answererId;
    }

    public int getNextTurnStartCountdownDelaySeconds() {
        return nextTurnStartCountdownDelaySeconds;
    }
}
