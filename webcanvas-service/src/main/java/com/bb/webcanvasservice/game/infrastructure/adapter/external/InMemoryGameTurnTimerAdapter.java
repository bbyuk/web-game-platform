package com.bb.webcanvasservice.game.infrastructure.adapter.external;

import com.bb.webcanvasservice.game.application.command.ProcessToNextTurnCommand;
import com.bb.webcanvasservice.game.application.service.GameService;
import com.bb.webcanvasservice.game.domain.port.external.GameTurnTimerPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryGameTurnTimerAdapter implements GameTurnTimerPort {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(8);
    private final Map<Long, ScheduledFuture<?>> futureMap = new ConcurrentHashMap<>();
    private final GameService gameService;

    @Override
    public void registerTurnTimer(ProcessToNextTurnCommand command) {
        stopTurnTimer(command.gameSessionId()); // 중복 등록 방지

        log.debug("command = {}", command);

        ScheduledFuture<?> future = scheduler.scheduleWithFixedDelay(
                () -> gameService.processToNextTurn(command),
                command.startDelaySeconds(),
                command.period(),
                TimeUnit.SECONDS
        );

        futureMap.put(command.gameSessionId(), future);
    }

    @Override
    public void stopTurnTimer(Long gameSessionId) {
        Optional.ofNullable(futureMap.remove(gameSessionId))
                .ifPresent(future -> future.cancel(true));
    }

}
