package com.bb.webcanvasservice.game.domain.event;

import com.bb.webcanvasservice.domain.shared.event.ApplicationEvent;

/**
 * 게임 턴이 넘어갔을 때 발행되는 이벤트
 */
public class GameTurnProgressedEvent extends ApplicationEvent {

    /**
     * 게임 방 ID
     */
    private final Long gameRoomId;
    /**
     * 게임 세션 ID
     */
    private final Long gameSessionId;

    /**
     * 게임 턴 ID
     */
    private final Long gameTurnId;

    public GameTurnProgressedEvent(Long gameRoomId, Long gameSessionId, Long gameTurnId) {
        super("SESSION/TURN_PROGRESSED");
        this.gameRoomId = gameRoomId;
        this.gameSessionId = gameSessionId;
        this.gameTurnId = gameTurnId;
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
}
