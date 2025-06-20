package com.bb.webcanvasservice.small.game.stub.service;

import com.bb.webcanvasservice.game.application.registry.GameSessionLoadRegistry;

public class GameGameSessionLoadRegistryStub implements GameSessionLoadRegistry {
    @Override
    public void register(Long gameSessionId, Long userId) {

    }

    @Override
    public void clear(Long gameSessionId) {

    }

    @Override
    public boolean isAllLoaded(Long gameSessionId, int enteredUserCount) {
        return false;
    }
}
