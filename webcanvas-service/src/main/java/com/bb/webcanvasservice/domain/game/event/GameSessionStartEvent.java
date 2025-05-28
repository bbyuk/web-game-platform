package com.bb.webcanvasservice.domain.game.event;

import com.bb.webcanvasservice.common.event.ApplicationEvent;
import lombok.Getter;

/**
 * 게임이 시작될 때 발행되는 이벤트
 */
@Getter
public class GameSessionStartEvent extends ApplicationEvent {

    /**
     * 게임 방 ID
     */
    private final Long gameRoomId;
    /**
     * 게임 세션 ID
     */
    private final Long gameSessionId;

    public GameSessionStartEvent(Long gameRoomId, Long gameSessionId) {
        super("SESSION/STARTED");
        this.gameRoomId = gameRoomId;
        this.gameSessionId = gameSessionId;
    }
}
