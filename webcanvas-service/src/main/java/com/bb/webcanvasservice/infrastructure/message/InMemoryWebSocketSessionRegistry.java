package com.bb.webcanvasservice.infrastructure.message;

import com.bb.webcanvasservice.infrastructure.websocket.registry.WebSocketSessionRegistry;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 웹소켓 세션 등록
 */
@Component
public class InMemoryWebSocketSessionRegistry implements WebSocketSessionRegistry {

    /**
     * 유저 ID : sessionId 테이블
     */
    private final Map<Long, String> userIdToSessionId = new ConcurrentHashMap<>();

    @Override
    public void register(Long userId, String sessionId) {
        userIdToSessionId.put(userId, sessionId);
    }

    @Override
    public void unregister(Long userId) {
        userIdToSessionId.remove(userId);
    }

    @Override
    public boolean hasSession(Long userId) {
        return userIdToSessionId.containsKey(userId);
    }

    @Override
    public void clear() {
        userIdToSessionId.clear();
    }
}
