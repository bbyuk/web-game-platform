package com.bb.webcanvasservice.infrastructure.persistence.game.repository;

import com.bb.webcanvasservice.domain.game.model.GamePlayHistory;
import com.bb.webcanvasservice.domain.game.repository.GamePlayHistoryRepository;
import com.bb.webcanvasservice.infrastructure.persistence.game.GameModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GamePlayHistoryRepositoryImpl implements GamePlayHistoryRepository {
    private final GamePlayHistoryJpaRepository gamePlayHistoryJpaRepository;

    @Override
    public List<GamePlayHistory> findByGameSessionId(Long gameSessionId) {
        return gamePlayHistoryJpaRepository.findByGameSessionId(gameSessionId)
                .stream()
                .map(GameModelMapper::toModel)
                .collect(Collectors.toList());
    }

}
