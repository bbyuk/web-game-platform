package com.bb.webcanvasservice.game.domain.event;

import org.springframework.context.ApplicationEvent;

/**
 * 게임 턴 타이머 등록 요청 이벤트
 */
public class GameTurnTimerRegisterRequestedEvent extends ApplicationEvent {

    // 게임 방 ID
    private final Long gameRoomId;

    // 게임 세션 ID
    private final Long gameSessionId;

    // 게임 턴 간격 초
    private final int period;

    // 정답 여부
    private final boolean answered;

    // 다음턴 시작 카운트다운 딜레이 초
    private final int nextTurnStartCountdownDelaySeconds;

    public GameTurnTimerRegisterRequestedEvent(Long gameRoomId, Long gameSessionId, int period, boolean answered, int nextTurnStartCountdownDelaySeconds) {
        super("SESSION/TURN/TIMER_REGISTER_REQUESTED");
        this.gameRoomId = gameRoomId;
        this.gameSessionId = gameSessionId;
        this.period = period;
        this.answered = answered;
        this.nextTurnStartCountdownDelaySeconds = nextTurnStartCountdownDelaySeconds;
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

    public int getNextTurnStartCountdownDelaySeconds() {
        return nextTurnStartCountdownDelaySeconds;
    }
}
