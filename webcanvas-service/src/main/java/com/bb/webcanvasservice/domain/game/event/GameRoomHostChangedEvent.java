package com.bb.webcanvasservice.domain.game.event;

import com.bb.webcanvasservice.common.event.ApplicationEvent;
import lombok.Getter;

/**
 * 게임 방 호스트 변경시 pub 이벤트
 */
@Getter
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
}
