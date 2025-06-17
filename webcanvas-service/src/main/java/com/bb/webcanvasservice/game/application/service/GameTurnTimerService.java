package com.bb.webcanvasservice.game.application.service;

import com.bb.webcanvasservice.game.application.registry.GameTurnTimerRegistry;
import com.bb.webcanvasservice.game.domain.exception.GameTurnTimerNotFoundException;
import com.bb.webcanvasservice.game.domain.model.GameTurnTimer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class GameTurnTimerService {
    private final GameTurnTimerRegistry gameTurnTimerRegistry;
    private final ScheduledExecutorService scheduler;

    /**
     * 게임 턴 타이머를 스케줄러에 등록한다.
     *
     * @param gameRoomId     게임 방 ID
     * @param period         타이머 간격
     * @param turnEndHandler 턴 종료시 작업할 핸들러
     */
    public void registerTurnTimer(Long gameRoomId, Long gameSessionId, int period, Consumer<Long> turnEndHandler) {
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(() -> {
            turnEndHandler.accept(gameSessionId);
        }, 0, period, TimeUnit.SECONDS);

        gameTurnTimerRegistry.register(gameRoomId, new GameTurnTimer(future, period, turnEndHandler));
    }

    /**
     * 게임 방 턴 타이머를 종료
     *
     * @param gameRoomId
     */
    public void stopTurnTimer(Long gameRoomId) {
        GameTurnTimer gameTurnTimer = gameTurnTimerRegistry.get(gameRoomId);
        if (gameTurnTimerRegistry.contains(gameRoomId)) {
            gameTurnTimer.stop();
        }

        gameTurnTimerRegistry.remove(gameRoomId);
    }

    /**
     * 게임 방 턴 타이머 초기화
     *
     * @param gameRoomId
     */
    public void resetTurnTimer(Long gameRoomId) {
        if (!gameTurnTimerRegistry.contains(gameRoomId)) {
            GameTurnTimerNotFoundException exception = new GameTurnTimerNotFoundException();
            throw exception;
        }

        GameTurnTimer oldTimer = gameTurnTimerRegistry.get(gameRoomId);
        stopTurnTimer(gameRoomId);


        // 새 타이머 등록
        ScheduledFuture<?> newTimerEngine = scheduler.scheduleAtFixedRate(
                () -> oldTimer.executeCallback(gameRoomId),
                0,
                oldTimer.getPeriod(),
                TimeUnit.SECONDS);

        gameTurnTimerRegistry.register(gameRoomId, oldTimer.recreateNewTimer(newTimerEngine));
    }

}
