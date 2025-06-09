package com.bb.webcanvasservice.domain.game.repository;

import com.bb.webcanvasservice.domain.game.enums.GameRoomEntranceState;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GameRoomEntranceCustomRepositoryImpl implements GameRoomEntranceCustomRepository {

    private final EntityManager em;

    @Override
    public boolean existsActiveEntrance(Long gameRoomId, Long userId) {
        String jpql = """
                select exists (
                     select 1
                     from   GameRoomEntrance gre
                     where  gre.gameRoom.id = :gameRoomId
                     and    gre.user.id = :userId
                     and    gre.state in :enteredStates
                )
                """;

        return em.createQuery(jpql, Boolean.class)
                .setParameter("enteredStates", GameRoomEntranceState.entered)
                .setParameter("gameRoomId", gameRoomId)
                .setParameter("userId", userId)
                .getSingleResult();
    }

    @Override
    public int findEnteredUserCount(Long gameRoomId) {

        String jpql = """
                        select  count(gre)
                        from    GameRoomEntrance gre
                        where   gre.gameRoom.id = :gameRoomId
                        and     gre.state in :enteredStates
                    """;
        return em.createQuery(jpql, Integer.class)
                .setParameter("gameRoomId", gameRoomId)
                .setParameter("enteredStates", GameRoomEntranceState.entered)
                .getSingleResult().intValue();
    }
}
