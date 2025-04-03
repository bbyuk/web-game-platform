package com.bb.webcanvasservice.security.websocket;

import com.bb.webcanvasservice.security.auth.JwtManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;


/**
 * 웹소켓 엔드포인트, 메시지 브로커 등 설정 클래스
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtManager jwtManager;
    private final WebSocketAuthenticationArgumentResolver webSocketAuthenticationArgumentResolver;

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

    /**
     * 웹소켓 Connect 및 인바운드 메세지 요청에 대한 인증 처리 intetceptor 등록
     * @param registration
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new JwtChannelInterceptor(jwtManager));
    }

    /**
     * 커스텀 Argument Resolver 등록.
     * WebSocket 핸들러에서 Custom Authentication 객체(WebCanvasAuthentication)를 주입받을 수 있도록 설정.
     * @param argumentResolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(webSocketAuthenticationArgumentResolver);
    }
}
