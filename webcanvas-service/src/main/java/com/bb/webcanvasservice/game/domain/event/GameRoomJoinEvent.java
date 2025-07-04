package com.bb.webcanvasservice.game.domain.event;

import com.bb.webcanvasservice.domain.shared.event.ApplicationEvent;

/**
 * 게임 방 입장 이벤트 발생시 pub 이벤트
 */
public class GameRoomJoinEvent extends ApplicationEvent {

    private final Long gameRoomId;
    private final Long userId;

    public GameRoomJoinEvent(Long gameRoomId, Long userId) {
        super("ROOM/JOIN");
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
