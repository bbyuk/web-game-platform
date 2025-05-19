package com.bb.webcanvasservice.domain.game.event;

import com.bb.webcanvasservice.common.event.ApplicationEvent;
import lombok.Getter;

/**
 * 게임 방 입장 이벤트 발생시 pub 이벤트
 */
@Getter
public class GameRoomEntranceEvent extends ApplicationEvent {
    private Long gameRoomId;

    public GameRoomEntranceEvent(Long gameRoomId) {
        this.event = "ROOM/ENTRANCE";
        this.gameRoomId = gameRoomId;
    }
}
