package com.bb.webcanvasservice.security.websocket;

import com.bb.webcanvasservice.security.auth.JwtManager;
import com.bb.webcanvasservice.security.auth.WebCanvasAuthentication;
import jakarta.servlet.http.HttpServletRequest;
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

    /**
     * 웹소켓 연결 시 인증 처리
     * 인증후 Authentication 객체를 SecurityContextHolder에 저장
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
            accessor.setUser(new WebCanvasAuthentication(userId));
        }


        return message;
    }


}
