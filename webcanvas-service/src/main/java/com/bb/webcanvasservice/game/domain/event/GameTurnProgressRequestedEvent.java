package com.bb.webcanvasservice.game.domain.event;

import com.bb.webcanvasservice.domain.shared.event.ApplicationEvent;

/**
 * 게임 턴 진행처리 요청 이벤트
 * pass or answered 시
 */
public class GameTurnProgressRequestedEvent extends ApplicationEvent {

    private final Long gameRoomId;
    private final Long gameSessionId;
    private final Long gameTurnId;
    private final Long answererId;

    public GameTurnProgressRequestedEvent(Long gameRoomId, Long gameSessionId, Long gameTurnId, Long answererId) {
        super("SESSION/TURN_PROGRESS_REQUESTED");
        this.gameRoomId = gameRoomId;
        this.gameSessionId = gameSessionId;
        this.gameTurnId = gameTurnId;
        this.answererId = answererId;
    }

    public GameTurnProgressRequestedEvent(Long gameRoomId, Long gameSessionId, Long gameTurnId) {
        this(gameRoomId, gameSessionId, gameTurnId, null);
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

    public Long getAnswererId() {
        return answererId;
    }
}
