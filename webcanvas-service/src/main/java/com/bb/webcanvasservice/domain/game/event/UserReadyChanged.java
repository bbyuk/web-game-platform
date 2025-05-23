package com.bb.webcanvasservice.domain.game.event;

import com.bb.webcanvasservice.common.event.ApplicationEvent;
import lombok.Getter;

/**
 * 유저의 레디 상태가 변경되었을 때 발행되는 이벤트
 */
@Getter
public class UserReadyChanged extends ApplicationEvent {

    private final Long gameRoomId;
    private final Long userId;
    private final boolean ready;

    public UserReadyChanged(Long gameRoomId, Long userId, boolean ready) {
        super("ROOM/USER_READY_CHANGED");
        this.gameRoomId = gameRoomId;
        this.userId = userId;
        this.ready = ready;
    }
}
