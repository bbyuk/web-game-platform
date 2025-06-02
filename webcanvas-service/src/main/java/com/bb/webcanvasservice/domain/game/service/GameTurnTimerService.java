package com.bb.webcanvasservice.domain.game.service;

import com.bb.webcanvasservice.domain.game.dto.inner.GameTurnTimerEntry;
import com.bb.webcanvasservice.domain.game.exception.GameTurnTimerNotFoundException;
import com.bb.webcanvasservice.domain.game.registry.GameTurnTimerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameTurnTimerService {

    private final GameTurnTimerRegistry gameTurnTimerRegistry;
    private final ScheduledExecutorService scheduler;

    /**
     * 게임 턴 타이머를 스케줄러에 등록한다.
     * @param gameRoomId 게임 방 ID
     * @param period 타이머 간격
     * @param turnEndHandler 턴 종료시 작업할 핸들러
     * @param gameEndChecker 게임 종료 여부 체크 메소드
     * @param gameEndHandler 게임 종료시 작업할 핸들러
     */
    public void registerTurnTimer(Long gameRoomId, Long gameSessionId, int period, Consumer<Long> turnEndHandler, Function<Long, Boolean> gameEndChecker, Consumer<Long> gameEndHandler) {
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(() -> {
            if (gameEndChecker.apply(gameSessionId)) {
                gameEndHandler.accept(gameSessionId);
            } else {
                turnEndHandler.accept(gameSessionId);
            }
        }, 0, period, TimeUnit.SECONDS);

        gameTurnTimerRegistry.register(gameRoomId, new GameTurnTimerEntry(future, period, turnEndHandler, gameEndChecker, gameEndHandler));
    }

    /**
     * 게임 방 턴 타이머를 종료
     * @param gameRoomId
     */
    public void stopTurnTimer(Long gameRoomId) {
        GameTurnTimerEntry gameTurnTimerEntry = gameTurnTimerRegistry.get(gameRoomId);
        if (gameTurnTimerRegistry.contains(gameRoomId)) {
            gameTurnTimerEntry.future().cancel(false);
        }

        gameTurnTimerRegistry.remove(gameRoomId);
    }

    /**
     * 게임 방 턴 타이머 초기화
     * @param gameRoomId
     */
    public void resetTurnTimer(Long gameRoomId) {
        if (!gameTurnTimerRegistry.contains(gameRoomId)) {
            GameTurnTimerNotFoundException exception = new GameTurnTimerNotFoundException();
            log.error(exception.getMessage());
            throw exception;
        }

        GameTurnTimerEntry oldEntry = gameTurnTimerRegistry.get(gameRoomId);
        stopTurnTimer(gameRoomId);

        // 새 타이머 등록
        ScheduledFuture<?> newTimer = scheduler.scheduleAtFixedRate(() -> {
            if (oldEntry.gameEndChecker().apply(gameRoomId)) {
                oldEntry.gameEndHandler().accept(gameRoomId);
            } else {
                oldEntry.turnEndHandler().accept(gameRoomId);
            }
        }, 0, oldEntry.period(), TimeUnit.SECONDS);

        gameTurnTimerRegistry.register(gameRoomId, new GameTurnTimerEntry(newTimer, oldEntry.period(), oldEntry.turnEndHandler(), oldEntry.gameEndChecker(), oldEntry.gameEndHandler()));
    }
}
