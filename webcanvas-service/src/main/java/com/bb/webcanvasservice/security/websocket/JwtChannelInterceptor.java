package com.bb.webcanvasservice.security.websocket;

import com.bb.webcanvasservice.security.JwtManager;
import com.bb.webcanvasservice.security.WebCanvasAuthentication;
import com.bb.webcanvasservice.security.exception.NotAuthenticatedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

import java.util.Optional;

/**
 * 웹소켓 연결 및 웹소켓 메시지 처리에 대한 인증 처리를 할 수 있는 ChannelInterceptor 구현체 클래스
 */
@Slf4j
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtManager jwtManager;

    /**
     * 웹소켓 연결 및 웹소켓 메시지에 대한 인증 처리
     * 인증후 Authentication 객체를 SecurityContextHolder에 저장
     * @param message
     * @param channel
     * @return
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        log.info("JwtChannelInterceptor preSend ====== {}", message);

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (!StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message;
        }

        Optional<String> optionalJwt = Optional.ofNullable(accessor.getFirstNativeHeader(JwtManager.BEARER_TOKEN));
        String token = optionalJwt.filter(header -> header.startsWith(JwtManager.TOKEN_PREFIX))
                .map(header -> header.substring(JwtManager.TOKEN_PREFIX.length() + 1))
                .filter(jwtManager::validateToken)
                .orElseThrow(() -> new NotAuthenticatedException("잘못된 토큰입니다."));

        Long userId = jwtManager.getUserIdFromToken(token);
        WebCanvasAuthentication webCanvasAuthentication = new WebCanvasAuthentication(userId);
        accessor.setUser(webCanvasAuthentication);

        return message;
    }


}
