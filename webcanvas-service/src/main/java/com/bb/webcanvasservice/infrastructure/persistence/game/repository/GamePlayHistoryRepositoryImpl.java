package com.bb.webcanvasservice.infrastructure.persistence.game.repository;

import com.bb.webcanvasservice.domain.game.model.GamePlayHistory;
import com.bb.webcanvasservice.domain.game.repository.GamePlayHistoryRepository;
import com.bb.webcanvasservice.infrastructure.persistence.game.GameModelMapper;
import com.bb.webcanvasservice.infrastructure.persistence.game.entity.GameSessionJpaEntity;
import com.bb.webcanvasservice.infrastructure.persistence.user.entity.UserJpaEntity;
import com.bb.webcanvasservice.infrastructure.persistence.user.repository.UserJpaRepository;
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
