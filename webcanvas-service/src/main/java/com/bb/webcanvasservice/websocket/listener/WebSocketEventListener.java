package com.bb.webcanvasservice.websocket.listener;

import com.bb.webcanvasservice.websocket.registry.SessionRegistry;
import com.bb.webcanvasservice.common.security.WebCanvasAuthentication;
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

    private final SessionRegistry sessionRegistry;

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
            sessionRegistry.unregister(userId);
            log.info("User {} unregistered from session {}", userId, sessionId);
        }
    }
}
