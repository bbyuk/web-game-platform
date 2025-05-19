package com.bb.webcanvasservice.domain.game.event;

import com.bb.webcanvasservice.common.event.ApplicationEvent;

public class GameRoomExitEvent extends ApplicationEvent {
    private Long gameRoomId;

    public GameRoomExitEvent(Long gameRoomId) {
        this.event = "ROOM/EXIT";
        this.gameRoomId = gameRoomId;
    }
}
