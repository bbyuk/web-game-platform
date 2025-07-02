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
    public void registerTurnTimer(ProcessToNextTurnCommand command, Consumer<ProcessToNextTurnCommand> turnEndHandler) {
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
                () -> {
                    try {
                        turnEndHandler.accept(command);
                    }
                    catch(Exception e) {
                        exceptionHandler.handle(e);
                    }
                }, 0,
                command.period(), TimeUnit.SECONDS);
        gameTurnTimerRegistry.register(command.gameRoomId(), new GameTurnTimer(future, command.period(), turnEndHandler));
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
     * @param command 커맨드
     */
    public void resetTurnTimer(ProcessToNextTurnCommand command) {
        if (!gameTurnTimerRegistry.contains(command.gameSessionId())) {
            GameTurnTimerNotFoundException exception = new GameTurnTimerNotFoundException();
            throw exception;
        }

        GameTurnTimer oldTimer = gameTurnTimerRegistry.get(command.gameRoomId());
        stopTurnTimer(command.gameRoomId());


        // 새 타이머 등록
        registerTurnTimer(command, (c) -> oldTimer.executeCallback(c));
    }

}
