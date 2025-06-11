package com.bb.webcanvasservice.infrastructure.persistence.game.registry;

import com.bb.webcanvasservice.application.game.dto.GameTurnTimerEntry;
import com.bb.webcanvasservice.domain.game.registry.GameTurnTimerRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class InMemoryGameTurnTimerRegistry implements GameTurnTimerRegistry {

    private final Map<Long, GameTurnTimerEntry> timerMap = new ConcurrentHashMap<>();

    @Override
    public void register(Long gameRoomId, GameTurnTimerEntry future) {
        timerMap.put(gameRoomId, future);
    }

    @Override
    public GameTurnTimerEntry get(Long gameRoomId) {
        return timerMap.get(gameRoomId);
    }

    @Override
    public void remove(Long gameRoomId) {
        timerMap.remove(gameRoomId);
    }

    @Override
    public boolean contains(Long gameRoomId) {
        return timerMap.containsKey(gameRoomId);
    }
}
