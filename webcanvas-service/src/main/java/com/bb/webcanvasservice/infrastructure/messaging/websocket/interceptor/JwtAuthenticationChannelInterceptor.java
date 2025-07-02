package com.bb.webcanvasservice.infrastructure.messaging.websocket.interceptor;

import com.bb.webcanvasservice.infrastructure.messaging.websocket.exception.AlreadyConnectedException;
import com.bb.webcanvasservice.common.messaging.websocket.WebSocketSessionRegistry;
import com.bb.webcanvasservice.common.util.JwtManager;
import com.bb.webcanvasservice.infrastructure.security.http.WebCanvasAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

/**
 * 웹소켓 연결 및 웹소켓 메시지 처리에 대한 인증 처리를 할 수 있는 ChannelInterceptor 구현체 클래스
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationChannelInterceptor implements ChannelInterceptor {

    private final JwtManager jwtManager;
    private final WebSocketSessionRegistry webSocketSessionRegistry;

    /**
     * 웹소켓 연결 시 인증 처리
     * 인증후 Authentication 객체를 SecurityContextHolder에 저장
     *
     * 같은 userid 토큰으로 중복입장 방어 로직 구현 (stateful)
     *      -> 매 메시지마다 토큰 검증이 필요없게됨
     *
     * 1. 헤더 토큰 파싱
     * 2. 토큰 validation
     * 3. sessionRegistry로 이미 연결을 보유중인 userId인지 체크
     * 4. 유저 인증 토큰 set
     * 5. sessionRegistry에 연결 요청 보낸 ID : 연결 세션 ID 매핑해 저장
     *
     * @param message
     * @param channel
     * @return
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        log.info("JwtChannelInterceptor preSend ====== {}", message);
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String accessToken = jwtManager.resolveBearerTokenValue(accessor.getFirstNativeHeader(JwtManager.BEARER_TOKEN));
            jwtManager.validateToken(accessToken);

            Long userId = jwtManager.getUserIdFromToken(accessToken);

            if (webSocketSessionRegistry.hasSession(userId)) {
                throw new AlreadyConnectedException();
            }

            accessor.setUser(new WebCanvasAuthentication(userId));
            webSocketSessionRegistry.register(userId, accessor.getSessionId());
        }


        return message;
    }


}
