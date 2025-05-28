package com.bb.webcanvasservice.domain.game.event;

import com.bb.webcanvasservice.common.event.ApplicationEvent;
import lombok.Getter;

/**
 * 게임이 시작될 때 발행되는 이벤트
 */
@Getter
public class GameSessionStartedEvent extends ApplicationEvent {

    /**
     * 게임 세션 ID
     */
    private final Long gameSessionId;

    public GameSessionStartedEvent(Long gameSessionId) {
        super("SESSION/STARTED");
        this.gameSessionId = gameSessionId;
    }
}
