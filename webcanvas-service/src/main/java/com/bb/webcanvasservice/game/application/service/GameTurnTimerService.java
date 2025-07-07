package com.bb.webcanvasservice.game.application.service;

import com.bb.webcanvasservice.common.exception.handler.AsyncExceptionHandler;
import com.bb.webcanvasservice.game.application.command.ProcessToNextTurnCommand;
import com.bb.webcanvasservice.game.application.registry.GameTurnTimerRegistry;
import com.bb.webcanvasservice.game.domain.exception.GameTurnTimerNotFoundException;
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
    private final AsyncExceptionHandler exceptionHandler;

    /**
     * 게임 턴 타이머를 스케줄러에 등록한다.
     *
     * @param command 턴 진행 command
     * @param turnEndHandler 턴 종료시 작업할 핸들러
     */
    public void registerTurnTimer(ProcessToNextTurnCommand command, Consumer<ProcessToNextTurnCommand> turnEndHandler, int startAfter) {
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
                () -> {
                    try {
                        turnEndHandler.accept(command);
                    }
                    catch(Exception e) {
                        exceptionHandler.handle(e);
                    }
                }, startAfter,
                command.period(), TimeUnit.SECONDS);
        gameTurnTimerRegistry.register(command.gameSessionId(), new GameTurnTimer(future, command.period(), turnEndHandler));
    }

    /**
     * 게임 세션 턴 타이머를 종료
     *
     * @param gameSessionId 게임 세션 ID
     */
    public void stopTurnTimer(Long gameSessionId) {
        GameTurnTimer gameTurnTimer = gameTurnTimerRegistry.get(gameSessionId);
        if (gameTurnTimerRegistry.contains(gameSessionId)) {
            gameTurnTimer.stop();
        }

        gameTurnTimerRegistry.remove(gameSessionId);
    }

    /**
     * 게임 방 턴 타이머 초기화
     *
     * @param command 커맨드
     */
    public void resetTurnTimer(ProcessToNextTurnCommand command) {
        if (!gameTurnTimerRegistry.contains(command.gameSessionId())) {
            throw new GameTurnTimerNotFoundException();
        }

        GameTurnTimer oldTimer = gameTurnTimerRegistry.get(command.gameSessionId());
        stopTurnTimer(command.gameSessionId());


        // 새 타이머 등록
        registerTurnTimer(command, oldTimer::executeCallback, command.period());
    }

}
