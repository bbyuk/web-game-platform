package com.bb.webcanvasservice.domain.game.repository;

import com.bb.webcanvasservice.domain.game.entity.GameRoomEntrance;
import com.bb.webcanvasservice.domain.game.entity.GameSession;
import com.bb.webcanvasservice.domain.game.enums.GameRoomEntranceState;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class GameSessionCustomRepositoryImpl implements GameSessionCustomRepository {

    private final EntityManager em;

    @Override
    @Transactional
    public boolean isAllUserLoaded(Long gameSessionId) {
        GameSession gameSession = em.find(GameSession.class, gameSessionId);
        Long gameRoomId = gameSession.getGameRoom().getId();
        String allEnteredUsersJpql = """
                     select count(gre)
                     from   GameRoomEntrance gre
                     where  gre.state in :entered
                     and    gre.state != :loading
                     and    gre.gameRoom.id = :gameRoomId
                """;
        return em.createQuery(allEnteredUsersJpql, Long.class)
                .setParameter("entered", GameRoomEntranceState.entered)
                .setParameter("loading", GameRoomEntranceState.LOADING)
                .setParameter("gameRoomId", gameRoomId)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getSingleResult() == 0;
    }
}
