package com.bb.webcanvasservice.domain.game.event;

import com.bb.webcanvasservice.common.event.ApplicationEvent;
import lombok.Getter;

/**
 * 게임 세션 종료시 발생하는 이벤트
 */
@Getter
public class GameSessionEndEvent extends ApplicationEvent {

    /**
     * 게임 session ID
     */
    private final Long gameSessionId;

    /**
     * 게임 방 ID
     */
    private final Long gameRoomId;

    public GameSessionEndEvent(Long gameSessionId, Long gameRoomId) {
        super("SESSION/END");
        this.gameSessionId = gameSessionId;
        this.gameRoomId = gameRoomId;
    }
}
