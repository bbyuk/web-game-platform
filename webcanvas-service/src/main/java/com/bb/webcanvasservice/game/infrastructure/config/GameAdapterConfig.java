package com.bb.webcanvasservice.game.infrastructure.config;

import com.bb.webcanvasservice.chat.domain.port.game.ChatGamePort;
import com.bb.webcanvasservice.game.domain.adapter.ChatGameAdapter;
import com.bb.webcanvasservice.game.domain.repository.GameSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * game domain adapter configuration
 */
@Configuration
@RequiredArgsConstructor
public class GameAdapterConfig {

    private final GameSessionRepository gameSessionRepository;

    private final ApplicationEventPublisher eventPublisher;

    @Bean
    public ChatGamePort chatGameCommandPort() {
        return new ChatGameAdapter(gameSessionRepository, eventPublisher);
    }
}
