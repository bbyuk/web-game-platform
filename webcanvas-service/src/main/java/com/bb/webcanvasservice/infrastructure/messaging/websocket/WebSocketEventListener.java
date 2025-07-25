package com.bb.webcanvasservice.infrastructure.messaging.websocket;

import com.bb.webcanvasservice.infrastructure.security.http.WebCanvasAuthentication;
import com.bb.webcanvasservice.common.messaging.websocket.WebSocketSessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * 웹소켓 이벤트 리스너
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final WebSocketSessionRegistry webSocketSessionRegistry;

    /**
     * 웹소켓 disconnect 이벤트 리스너
     * @param event
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();

        log.debug("Disconnected session: {}", sessionId);

        // user 정보가 Authentication 에 담겨 있을 경우
        if (accessor.getUser() instanceof WebCanvasAuthentication auth) {
            Long userId = auth.getUserId();
            webSocketSessionRegistry.unregister(userId);
            log.info("User {} unregistered from session {}", userId, sessionId);
        }
    }
}
