package com.bb.webcanvasservice.game.domain.event;

import com.bb.webcanvasservice.domain.shared.event.ApplicationEvent;

/**
 * 게임이 시작될 때 발행되는 이벤트
 */
public class GameSessionStartEvent extends ApplicationEvent {

    /**
     * 게임 방 ID
     */
    private final Long gameRoomId;
    /**
     * 게임 세션 ID
     */
    private final Long gameSessionId;

    public GameSessionStartEvent(Long gameRoomId, Long gameSessionId) {
        super("ROOM/SESSION_STARTED");
        this.gameRoomId = gameRoomId;
        this.gameSessionId = gameSessionId;
    }

    public Long getGameRoomId() {
        return gameRoomId;
    }

    public Long getGameSessionId() {
        return gameSessionId;
    }
}
