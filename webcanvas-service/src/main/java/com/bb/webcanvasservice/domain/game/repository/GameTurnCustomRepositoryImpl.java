package com.bb.webcanvasservice.domain.game.repository;

import com.bb.webcanvasservice.domain.game.entity.GameTurn;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GameTurnCustomRepositoryImpl implements GameTurnCustomRepository {

    private final EntityManager em;

    @Override
    public Optional<GameTurn> findLastTurn(Long gameSessionId) {
        String jpql = """
                select      gt
                from        GameTurn gt
                where       gt.gameSession.id =: gameSessionId
                order by    gt.id asc
                """;

        return Optional.of(
                em.createQuery(jpql, GameTurn.class)
                        .setParameter("gameSessionId", gameSessionId)
                        .setMaxResults(1)
                        .getSingleResult()
        );
    }
}
