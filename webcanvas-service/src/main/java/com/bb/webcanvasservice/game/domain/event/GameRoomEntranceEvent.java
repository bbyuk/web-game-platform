package com.bb.webcanvasservice.game.domain.event;

import com.bb.webcanvasservice.common.event.ApplicationEvent;

/**
 * 게임 방 입장 이벤트 발생시 pub 이벤트
 */
public class GameRoomEntranceEvent extends ApplicationEvent {

    private final Long gameRoomId;
    private final Long userId;

    public GameRoomEntranceEvent(Long gameRoomId, Long userId) {
        super("ROOM/ENTRANCE");
        this.gameRoomId = gameRoomId;
        this.userId = userId;
    }

    public Long getGameRoomId() {
        return gameRoomId;
    }

    public Long getUserId() {
        return userId;
    }
}
