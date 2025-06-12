package com.bb.webcanvasservice.infrastructure.persistence.game.registry;

import com.bb.webcanvasservice.domain.game.model.GameTurnTimer;
import com.bb.webcanvasservice.domain.game.registry.GameTurnTimerRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class InMemoryGameTurnTimerRegistry implements GameTurnTimerRegistry {

    private final Map<Long, GameTurnTimer> timerMap = new ConcurrentHashMap<>();

    @Override
    public void register(Long gameRoomId, GameTurnTimer future) {
        timerMap.put(gameRoomId, future);
    }

    @Override
    public GameTurnTimer get(Long gameRoomId) {
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
