package com.bb.webcanvasservice.game.infrastructure.persistence.registry;

import com.bb.webcanvasservice.game.domain.model.gameroom.GameTurnTimer;
import com.bb.webcanvasservice.game.application.registry.GameTurnTimerRegistry;
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
