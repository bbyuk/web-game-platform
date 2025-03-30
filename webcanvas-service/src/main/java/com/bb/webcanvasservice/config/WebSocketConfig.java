package com.bb.webcanvasservice.config;

import com.bb.webcanvasservice.security.JwtTokenManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 메세지 브로커 설정
     * @param registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메시지 브로커 활성화: "/canvas"과 "/chat"로 시작하는 경로에 대해 브로커를 활성화
        registry.enableSimpleBroker("/canvas", "/chat");

        // 클라이언트로 메시지를 보낼 때 사용할 prefix 설정 (필요시)
//        registry.setApplicationDestinationPrefixes("/canvas");
    }

    /**
     * WebSocket 엔드포인드 설정
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        /**
         * canvas 웹 소켓 서버 엔드포인트 지정
         */
        registry.addEndpoint("/canvas")
                .setAllowedOriginPatterns("*");
//                .withSockJS();
    }

}
