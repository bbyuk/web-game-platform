package com.bb.webcanvasservice.security;

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

    private final JwtTokenManager jwtTokenManager;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        log.info("JwtChannelInterceptor preSend ====== {}", message.getPayload());

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (!StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message;
        }

        Optional<String> optionalJwt = Optional.ofNullable(accessor.getFirstNativeHeader(JwtTokenManager.BEARER_TOKEN));
        String token = optionalJwt.filter(header -> header.startsWith(JwtTokenManager.TOKEN_PREFIX))
                .map(header -> header.substring(JwtTokenManager.TOKEN_PREFIX.length()))
                .filter(jwtTokenManager::validateToken)
                .orElseThrow(() -> new NotAuthenticatedException("잘못된 토큰입니다."));

        Long userId = jwtTokenManager.getUserIdFromToken(token);
        WebCanvasAuthentication webCanvasAuthentication = new WebCanvasAuthentication(userId);
        accessor.setUser(webCanvasAuthentication);

        return message;
    }


}
