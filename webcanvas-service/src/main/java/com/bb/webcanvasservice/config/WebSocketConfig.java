package com.bb.webcanvasservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // 메시지 브로커 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메시지 브로커 활성화: "/topic"과 "/queue"로 시작하는 경로에 대해 브로커를 활성화
        registry.enableSimpleBroker("/topic", "/sample");

        // 클라이언트로 메시지를 보낼 때 사용할 prefix 설정 (필요시)
        registry.setApplicationDestinationPrefixes("/app");
    }

    // WebSocket 엔드포인트 설정
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
//                .withSockJS();
    }
}
