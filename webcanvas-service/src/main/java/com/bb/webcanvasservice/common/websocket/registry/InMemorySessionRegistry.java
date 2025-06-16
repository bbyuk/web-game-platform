package com.bb.webcanvasservice.common.websocket.registry;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemorySessionRegistry implements SessionRegistry {

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
