package com.bb.webcanvasservice.game.application.config;

import com.bb.webcanvasservice.game.domain.registry.GameTurnTimerRegistry;
import com.bb.webcanvasservice.game.domain.repository.GameRoomEntranceRepository;
import com.bb.webcanvasservice.game.domain.repository.GameRoomRepository;
import com.bb.webcanvasservice.game.domain.repository.GameSessionRepository;
import com.bb.webcanvasservice.game.domain.service.GameRoomService;
import com.bb.webcanvasservice.game.domain.service.GameService;
import com.bb.webcanvasservice.game.domain.service.GameTurnTimerService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ScheduledExecutorService;

/**
 * game domain application layer configuration
 */
@Configuration
@RequiredArgsConstructor
public class GameApplicationConfig {

    private final GameRoomEntranceRepository gameRoomEntranceRepository;
    private final GameRoomRepository gameRoomRepository;
    private final GameSessionRepository gameSessionRepository;

    private final GameTurnTimerRegistry gameTurnTimerRegistry;
    private final ScheduledExecutorService scheduler;

    @Bean
    public GameRoomService gameRoomService() {
        return new GameRoomService(gameRoomRepository, gameRoomEntranceRepository);
    }

    @Bean
    public GameService gameService() {
        return new GameService(gameSessionRepository, gameRoomEntranceRepository);
    }

    @Bean
    public GameTurnTimerService gameTurnTimerService() {
        return new GameTurnTimerService(gameTurnTimerRegistry, scheduler);
    }
}
