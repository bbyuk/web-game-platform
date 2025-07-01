package com.bb.webcanvasservice.game.domain.event;

import com.bb.webcanvasservice.domain.shared.event.ApplicationEvent;

/**
 * 게임 방 호스트 변경시 pub 이벤트
 */
public class GameRoomHostChangedEvent extends ApplicationEvent {

    /**
     * 게임 방 ID
     */
    private Long gameRoomId;

    /**
     * 변경된 호스트 유저 ID
     */
    private Long hostUserId;

    public GameRoomHostChangedEvent(Long gameRoomId, Long userId) {
        super("ROOM/HOST_CHANGED");
        this.gameRoomId = gameRoomId;
        this.hostUserId = userId;
    }

    public Long getGameRoomId() {
        return gameRoomId;
    }

    public Long getHostUserId() {
        return hostUserId;
    }
}
