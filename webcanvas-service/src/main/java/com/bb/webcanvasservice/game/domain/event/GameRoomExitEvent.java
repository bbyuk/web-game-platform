package com.bb.webcanvasservice.game.domain.event;

import com.bb.webcanvasservice.common.event.ApplicationEvent;

/**
 * 게임 방 퇴장 이벤트 발생시 pub 이벤트
 */
public class GameRoomExitEvent extends ApplicationEvent {

    /**
     * 게임 방 ID
     */
    private final Long gameRoomId;

    /**
     * 유저 ID
     */
    private final Long userId;

    public GameRoomExitEvent(Long gameRoomId, Long userId) {
        super("ROOM/EXIT");
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
