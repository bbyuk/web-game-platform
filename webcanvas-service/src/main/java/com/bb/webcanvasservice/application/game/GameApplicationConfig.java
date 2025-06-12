package com.bb.webcanvasservice.application.game;

import com.bb.webcanvasservice.domain.game.registry.GameTurnTimerRegistry;
import com.bb.webcanvasservice.domain.game.repository.GameRoomEntranceRepository;
import com.bb.webcanvasservice.domain.game.repository.GameRoomRepository;
import com.bb.webcanvasservice.domain.game.repository.GameSessionRepository;
import com.bb.webcanvasservice.domain.game.service.GameRoomService;
import com.bb.webcanvasservice.domain.game.service.GameService;
import com.bb.webcanvasservice.domain.game.service.GameTurnTimerService;
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
