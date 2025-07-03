package com.bb.webcanvasservice.game.infrastructure.config;

import com.bb.webcanvasservice.chat.domain.port.game.ChatGameCommandPort;
import com.bb.webcanvasservice.game.application.repository.GameRoomRepository;
import com.bb.webcanvasservice.game.domain.adapter.ChatGameCommandAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * game domain adapter configuration
 */
@Configuration
@RequiredArgsConstructor
public class GameAdapterConfig {

    private final GameRoomRepository gameRoomRepository;

    @Bean
    public ChatGameCommandPort chatGameCommandPort() {
        return new ChatGameCommandAdapter(gameRoomRepository);
    }
}
