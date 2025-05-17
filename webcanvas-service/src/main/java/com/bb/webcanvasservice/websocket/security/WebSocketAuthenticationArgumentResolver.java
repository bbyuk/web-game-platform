package com.bb.webcanvasservice.websocket.security;

import com.bb.webcanvasservice.domain.auth.Authenticated;
import com.bb.webcanvasservice.domain.auth.WebCanvasAuthentication;
import com.bb.webcanvasservice.web.security.exception.ApplicationAuthenticationException;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;

import static com.bb.webcanvasservice.common.code.ErrorCode.AUTH_USER_NOT_FOUND;

/**
 * WebSocket 핸들러에서 Principal 대신 Custom Authentication 객체인 WebCanvasAuthentication를 주입받기 위한 ArgumentResolver.
 * Spring Security의 Principal을 WebCanvasAuthentication으로 변환하여 컨트롤러 핸들러 메서드에서 직접 사용할 수 있도록 함.
 */
@Component
public class WebSocketAuthenticationArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * 지원하는 파라미터 타입을 지정.
     * 여기서는 Principal 타입의 파라미터를 처리하도록 설정.
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(Authenticated.class) != null &&
                WebCanvasAuthentication.class.isAssignableFrom(parameter.getParameterType());
    }

    /**
     * WebSocket 메시지에서 Principal 정보를 가져와 Custom Authentication 객체(WebCanvasAuthentication)로 변환.
     * @param parameter 핸들러 메서드의 파라미터 정보
     * @param message WebSocket 메시지
     * @return WebCanvasAuthentication 객체
     * @throws Exception 인증되지 않은 사용자인 경우 예외 발생
     */
    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  Message<?> message) throws Exception {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        Principal principal = accessor.getUser();

        if (principal instanceof WebCanvasAuthentication authentication) {
            return authentication;
        }
        throw new ApplicationAuthenticationException(AUTH_USER_NOT_FOUND);
    }
}
