package com.bb.webcanvasservice.game.domain.event;

import org.springframework.context.ApplicationEvent;

/**
 * 게임 턴 타이머 등록 요청 이벤트
 */
public class GameTurnTimerRegisterRequestedEvent extends ApplicationEvent {

    private final Long gameRoomId;

    private final Long gameSessionId;

    private final int period;

    private final boolean answered;

    public GameTurnTimerRegisterRequestedEvent(Long gameRoomId, Long gameSessionId, int period, boolean answered) {
        super("SESSION/TURN/TIMER_REGISTER_REQUESTED");
        this.gameRoomId = gameRoomId;
        this.gameSessionId = gameSessionId;
        this.period = period;
        this.answered = answered;
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
