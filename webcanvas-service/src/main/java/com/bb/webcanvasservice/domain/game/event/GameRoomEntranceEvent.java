package com.bb.webcanvasservice.domain.game.event;

import com.bb.webcanvasservice.common.event.ApplicationEvent;
import lombok.Getter;

/**
 * 게임 방 입장 이벤트 발생시 pub 이벤트
 */
@Getter
public class GameRoomEntranceEvent extends ApplicationEvent {

    private final Long gameRoomId;
    private final Long userId;

    public GameRoomEntranceEvent(Long gameRoomId, Long userId) {
        super("ROOM/ENTRANCE");
        this.gameRoomId = gameRoomId;
        this.userId = userId;
    }
}
