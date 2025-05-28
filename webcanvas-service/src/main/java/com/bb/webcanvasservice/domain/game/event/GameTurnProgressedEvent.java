package com.bb.webcanvasservice.domain.game.event;

import com.bb.webcanvasservice.common.event.ApplicationEvent;
import lombok.Getter;

/**
 * 게임 턴이 넘어갔을 때 발행되는 이벤트
 */
@Getter
public class GameTurnProgressedEvent extends ApplicationEvent {

    /**
     * 게임 방 ID
     */
    private final Long gameRoomId;
    /**
     * 게임 세션 ID
     */
    private final Long gameSessionId;

    /**
     * 게임 턴 ID
     */
    private final Long gameTurnId;

    public GameTurnProgressedEvent(Long gameRoomId, Long gameSessionId, Long gameTurnId) {
        super("SESSION/TURN_PROGRESSED");
        this.gameRoomId = gameRoomId;
        this.gameSessionId = gameSessionId;
        this.gameTurnId = gameTurnId;
    }
}
