package com.bb.webcanvasservice.application.chat.config;

import com.bb.webcanvasservice.common.message.MessageSender;
import com.bb.webcanvasservice.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * chat domain application configuration class
 */
@Configuration
@RequiredArgsConstructor
public class ChatApplicationConfig {

    private final MessageSender messageSender;

    @Bean
    public ChatService chatService() {
        return new ChatService(messageSender);
    }
}
