package com.bb.webcanvasservice.domain.game.repository;

import com.bb.webcanvasservice.domain.game.entity.GameTurn;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class GameTurnCustomRepositoryImpl implements GameTurnCustomRepository {

    private final EntityManager em;

    @Override
    public Optional<GameTurn> findLastTurn(Long gameSessionId) {
        try {
            String jpql = """
                    select      gt
                    from        GameTurn gt
                    where       gt.gameSession.id =: gameSessionId
                    order by    gt.id asc
                    """;

            return em.createQuery(jpql, GameTurn.class)
                    .setParameter("gameSessionId", gameSessionId)
                    .setMaxResults(1)
                    .getResultList()
                    .stream()
                    .findFirst();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Optional.empty();
        }
    }
}
