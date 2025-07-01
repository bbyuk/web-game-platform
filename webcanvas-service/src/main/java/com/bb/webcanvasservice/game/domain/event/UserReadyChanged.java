package com.bb.webcanvasservice.game.domain.event;

import com.bb.webcanvasservice.domain.shared.event.ApplicationEvent;

/**
 * 유저의 레디 상태가 변경되었을 때 발행되는 이벤트
 */
public class UserReadyChanged extends ApplicationEvent {

    /**
     * 게임 방 ID
     */
    private final Long gameRoomId;

    /**
     * 유저 ID
     */
    private final Long userId;

    /**
     * 레디 여부
     */
    private final boolean ready;

    public UserReadyChanged(Long gameRoomId, Long userId, boolean ready) {
        super("ROOM/USER_READY_CHANGED");
        this.gameRoomId = gameRoomId;
        this.userId = userId;
        this.ready = ready;
    }

    public Long getGameRoomId() {
        return gameRoomId;
    }

    public Long getUserId() {
        return userId;
    }

    public boolean isReady() {
        return ready;
    }
}
