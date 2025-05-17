package com.bb.webcanvasservice.config;

import com.bb.webcanvasservice.config.properties.WebSocketProperties;
import com.bb.webcanvasservice.domain.game.GameRoomService;
import com.bb.webcanvasservice.common.JwtManager;
import com.bb.webcanvasservice.websocket.interceptor.JwtAuthenticationChannelInterceptor;
import com.bb.webcanvasservice.websocket.interceptor.SubscribeChannelInterceptor;
import com.bb.webcanvasservice.websocket.registry.SessionRegistry;
import com.bb.webcanvasservice.websocket.security.WebSocketAuthenticationArgumentResolver;
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

    private final WebSocketProperties webSocketProperties;
    private final JwtManager jwtManager;
    private final WebSocketAuthenticationArgumentResolver webSocketAuthenticationArgumentResolver;
    private final SessionRegistry sessionRegistry;

    /**
     * 게임 방과 연관되어 있는 웹소켓 이벤트 브로커 구독 요청의 validation 처리를 위한 서비스 주입
     */
    private final GameRoomService gameRoomService;

    /**
     * 메세지 브로커 설정
     * @param registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메시지 브로커 활성화
        registry.enableSimpleBroker(webSocketProperties.enabledBrokers().toArray(String[]::new));

        // 클라이언트로 메시지를 보낼 때 사용할 prefix 설정 (필요시)
        // registry.setApplicationDestinationPrefixes("");
    }

    /**
     * WebSocket connect 엔드포인드 설정
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        /**
         * canvas 웹 소켓 서버 엔드포인트 지정
         * canvas와 chat 모두 연결은 단일 엔드포인트로
         * 방 구분은 논리적으로 분리된 각각의 브로커 구독으로 구분
         */
        registry.addEndpoint(webSocketProperties.endpoint())
                .setAllowedOriginPatterns(webSocketProperties.allowedOriginPatterns().toArray(String[]::new));
//                .withSockJS();
    }

    /**
     * 웹소켓 Connect 및 인바운드 메세지 요청에 대한 인증 처리 intetceptor 등록
     * @param registration
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(
                new JwtAuthenticationChannelInterceptor(jwtManager, sessionRegistry),
                new SubscribeChannelInterceptor(gameRoomService)
        );
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
