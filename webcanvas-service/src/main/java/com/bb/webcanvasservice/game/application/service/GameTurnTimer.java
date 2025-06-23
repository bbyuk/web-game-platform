package com.bb.webcanvasservice.game.application.service;

import com.bb.webcanvasservice.game.application.command.ProcessToNextTurnCommand;

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
    private final Consumer<ProcessToNextTurnCommand> turnEndHandler;

    public GameTurnTimer(ScheduledFuture<?> engine, int period, Consumer<ProcessToNextTurnCommand> turnEndHandler) {
        this.engine = engine;
        this.period = period;
        this.turnEndHandler = turnEndHandler;
    }

    public void stop() {
        engine.cancel(false);
    }

    public void executeCallback(ProcessToNextTurnCommand command) {
        turnEndHandler.accept(command);
    }

    public int getPeriod() {
        return period;
    }
}
