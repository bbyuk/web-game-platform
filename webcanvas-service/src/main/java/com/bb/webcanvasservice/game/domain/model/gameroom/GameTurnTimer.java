package com.bb.webcanvasservice.game.domain.model.gameroom;

import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;

/**
 * 게임 턴 타이머 도메인 모델
 */
public class GameTurnTimer {

    /**
     * 타이머 엔진
     */
    private final ScheduledFuture<?> engine;

    /**
     * 타이머 간격
     */
    private final int period;

    /**
     * 턴 종료시 작업할 핸들러
     */
    private final Consumer<Long> turnEndHandler;

    public GameTurnTimer(ScheduledFuture<?> engine, int period, Consumer<Long> turnEndHandler) {
        this.engine = engine;
        this.period = period;
        this.turnEndHandler = turnEndHandler;
    }

    public void stop() {
        engine.cancel(false);
    }

    public void executeCallback(Long parameter) {
        turnEndHandler.accept(parameter);
    }

    public int getPeriod() {
        return period;
    }

    /**
     * 새로 스케쥴된 타이머 엔진으로 새 타이머 객체를 생성한다.
     * @param newEngine ScheduledExecutorService scheduler에 의해 스케쥴된 엔진
     * @return 새 타이머
     */
    public GameTurnTimer recreateNewTimer(ScheduledFuture<?> newEngine) {
        return new GameTurnTimer(newEngine, this.period, this.turnEndHandler);
    }
}
