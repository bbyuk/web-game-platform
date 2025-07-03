package com.bb.webcanvasservice.game.domain.event;

import com.bb.webcanvasservice.common.event.ApplicationEvent;

/**
 * 게임 세션 종료시 발생하는 이벤트
 */
public class GameSessionEndEvent extends ApplicationEvent {

    /**
     * 게임 session ID
     */
    private final Long gameSessionId;

    /**
     * 게임 방 ID
     */
    private final Long gameRoomId;

    public GameSessionEndEvent(Long gameSessionId, Long gameRoomId) {
        super("SESSION/END");
        this.gameSessionId = gameSessionId;
        this.gameRoomId = gameRoomId;
    }

    public Long getGameSessionId() {
        return gameSessionId;
    }

    public Long getGameRoomId() {
        return gameRoomId;
    }
}
