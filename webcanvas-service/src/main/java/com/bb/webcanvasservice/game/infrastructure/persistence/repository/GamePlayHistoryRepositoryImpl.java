package com.bb.webcanvasservice.game.infrastructure.persistence.repository;

import com.bb.webcanvasservice.game.domain.model.GamePlayHistory;
import com.bb.webcanvasservice.game.application.repository.GamePlayHistoryRepository;
import com.bb.webcanvasservice.game.infrastructure.persistence.entity.GameSessionJpaEntity;
import com.bb.webcanvasservice.game.infrastructure.persistence.mapper.GameModelMapper;
import com.bb.webcanvasservice.user.infrastructure.persistence.entity.UserJpaEntity;
import com.bb.webcanvasservice.user.infrastructure.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GamePlayHistoryRepositoryImpl implements GamePlayHistoryRepository {
    private final GamePlayHistoryJpaRepository gamePlayHistoryJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final GameSessionJpaRepository gameSessionJpaRepository;

    @Override
    public List<GamePlayHistory> findByGameSessionId(Long gameSessionId) {
        return gamePlayHistoryJpaRepository.findByGameSessionId(gameSessionId)
                .stream()
                .map(GameModelMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void saveAll(List<GamePlayHistory> gamePlayHistories) {
        Set<Long> gameSessionIds = gamePlayHistories.stream().map(GamePlayHistory::getGameSessionId).collect(Collectors.toSet());
        Set<Long> userIds = gamePlayHistories.stream().map(GamePlayHistory::getUserId).collect(Collectors.toSet());

        Map<Long, GameSessionJpaEntity> gameSessionMap = gameSessionJpaRepository.findAllById(gameSessionIds).stream().collect(Collectors.toMap(GameSessionJpaEntity::getId, Function.identity()));
        Map<Long, UserJpaEntity> userMap = userJpaRepository.findAllById(userIds).stream().collect(Collectors.toMap(UserJpaEntity::getId, Function.identity()));

        gamePlayHistoryJpaRepository.saveAll(
                gamePlayHistories.stream()
                        .map(gamePlayHistory ->
                                GameModelMapper.toEntity(
                                        gamePlayHistory,
                                        userMap.get(gamePlayHistory.getGameSessionId()),
                                        gameSessionMap.get(gamePlayHistory.getGameSessionId())
                                )
                        )
                        .toList()
        );
    }
}
