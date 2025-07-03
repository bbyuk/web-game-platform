package com.bb.webcanvasservice.small.game.stub.service;

import com.bb.webcanvasservice.game.domain.repository.GamePlayHistoryRepository;
import com.bb.webcanvasservice.game.domain.model.GamePlayHistory;

import java.util.List;

public class GameGamePlayHistoryRepositoryStub implements GamePlayHistoryRepository {
    @Override
    public List<GamePlayHistory> findByGameSessionId(Long gameSessionId) {
        return null;
    }

    @Override
    public void saveAll(List<GamePlayHistory> gamePlayHistories) {

    }
}
