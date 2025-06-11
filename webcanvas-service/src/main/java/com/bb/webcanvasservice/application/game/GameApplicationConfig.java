package com.bb.webcanvasservice.application.game;

import com.bb.webcanvasservice.domain.game.repository.GameRoomEntranceRepository;
import com.bb.webcanvasservice.domain.game.service.GameRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * game domain application layer configuration
 */
@Configuration
@RequiredArgsConstructor
public class GameApplicationConfig {

    private final GameRoomEntranceRepository gameRoomEntranceRepository;

    @Bean
    public GameRoomService gameRoomService() {
        return new GameRoomService(gameRoomEntranceRepository);
    }
}
