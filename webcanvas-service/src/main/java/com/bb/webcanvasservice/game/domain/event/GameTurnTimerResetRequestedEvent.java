package com.bb.webcanvasservice.game.domain.event;

import org.springframework.context.ApplicationEvent;

/**
 * 턴 타이머 리셋 요청 이벤트
 */
public class GameTurnTimerResetRequestedEvent extends ApplicationEvent {
    
    private Long gameRoomId;
    private Long gameSessionId;
    private int period;
    private boolean answered;
    
    public GameTurnTimerResetRequestedEvent(Long gameRoomId, Long gameSessionId, int period, boolean answered) {
        super("SESSION/TURN/TIMER_RESET_REQUESTED");
    }

    public Long getGameRoomId() {
        return gameRoomId;
    }

    public Long getGameSessionId() {
        return gameSessionId;
    }

    public int getPeriod() {
        return period;
    }

    public boolean isAnswered() {
        return answered;
    }
}
