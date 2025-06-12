package com.bb.webcanvasservice.infrastructure.persistence.game.repository;

import com.bb.webcanvasservice.domain.game.exception.GameRoomNotFoundException;
import com.bb.webcanvasservice.domain.game.exception.GameSessionNotFoundException;
import com.bb.webcanvasservice.domain.game.model.GameSession;
import com.bb.webcanvasservice.domain.game.model.GameTurn;
import com.bb.webcanvasservice.domain.game.repository.GameSessionRepository;
import com.bb.webcanvasservice.infrastructure.persistence.game.GameModelMapper;
import com.bb.webcanvasservice.infrastructure.persistence.game.entity.GameTurnJpaEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class GameSessionRepositoryImpl implements GameSessionRepository {

    private final GameSessionJpaRepository gameSessionJpaRepository;
    private final GameRoomJpaRepository gameRoomJpaRepository;
    private final GameTurnJpaRepository gameTurnJpaRepository;

    private final EntityManager em;

    @Override
    public Optional<GameSession> findById(Long gameSessionId) {
        return gameSessionJpaRepository.findById(gameSessionId).map(GameModelMapper::toModel);
    }

    @Override
    public GameSession save(GameSession gameSession) {
        return GameModelMapper.toModel(
                gameSessionJpaRepository.save(
                        GameModelMapper.toEntity(
                                gameSession,
                                gameRoomJpaRepository.findById(gameSession.getGameRoomId()).orElseThrow(GameRoomNotFoundException::new)
                        )
                )
        );
    }

    @Override
    public int findCurrentRound(Long gameSessionId) {
        return gameSessionJpaRepository.findCurrentRound(gameSessionId);
    }

    @Override
    public List<GameSession> findGameSessionsByGameRoomId(Long gameRoomId) {
        return gameSessionJpaRepository.findGameSessionsByGameRoomId(gameRoomId)
                .stream()
                .map(GameModelMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<GameTurn> findLatestTurn(Long gameSessionId) {
        try {
            String jpql = """
                    select      gt
                    from        GameTurnJpaEntity gt
                    where       gt.gameSessionEntity.id =: gameSessionId
                    order by    gt.id desc
                    """;

            return em.createQuery(jpql, GameTurnJpaEntity.class)
                    .setParameter("gameSessionId", gameSessionId)
                    .setMaxResults(1)
                    .getResultList()
                    .stream()
                    .findFirst()
                    .map(GameModelMapper::toModel);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public List<GameTurn> findTurnsByGameSessionId(Long gameSessionId) {
        return gameTurnJpaRepository.findTurnsByGameSessionId(gameSessionId)
                .stream()
                .map(GameModelMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public long findTurnCountByGameSessionId(Long gameSessionId) {
        return gameTurnJpaRepository.findTurnCountByGameSessionId(gameSessionId);
    }

    @Override
    public GameTurn saveGameTurn(GameTurn gameTurn) {
        return GameModelMapper.toModel(
                gameTurnJpaRepository.save(
                        GameModelMapper.toEntity(
                                gameTurn,
                                gameSessionJpaRepository.findById(gameTurn.getGameSessionId()).orElseThrow(GameSessionNotFoundException::new)
                        )
                )
        );
    }
}
