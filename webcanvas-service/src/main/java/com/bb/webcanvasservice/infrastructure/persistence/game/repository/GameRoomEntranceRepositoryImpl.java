package com.bb.webcanvasservice.infrastructure.persistence.game.repository;

import com.bb.webcanvasservice.domain.game.model.GameRoomEntrance;
import com.bb.webcanvasservice.domain.game.model.GameRoomEntranceState;
import com.bb.webcanvasservice.domain.game.repository.GameRoomEntranceRepository;
import com.bb.webcanvasservice.infrastructure.persistence.game.GameModelMapper;
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
public class GameRoomEntranceRepositoryImpl implements GameRoomEntranceRepository {
    private final EntityManager em;
    private final GameRoomEntranceJpaRepository jpaRepository;

    @Override
    public boolean existsActiveEntrance(Long gameRoomId, Long userId) {
        String jpql = """
                select exists (
                     select 1
                     from   GameRoomEntranceJpaEntity gre
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
                        from    GameRoomEntranceJpaEntity gre
                        where   gre.gameRoom.id = :gameRoomId
                        and     gre.state in :enteredStates
                    """;
        return em.createQuery(jpql, Long.class)
                .setParameter("gameRoomId", gameRoomId)
                .setParameter("enteredStates", GameRoomEntranceState.entered)
                .getSingleResult().intValue();
    }

    @Override
    public boolean existsGameRoomEntranceByUserId(Long userId) {
        return jpaRepository.existsGameRoomEntranceByUserId(userId);
    }

    @Override
    public Optional<GameRoomEntrance> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId)
                .map(GameModelMapper::toModel);
    }

    @Override
    public List<GameRoomEntrance> findGameRoomEntrancesByGameRoomIdWithLock(Long gameRoomId) {
        return jpaRepository.findGameRoomEntrancesByGameRoomIdWithLock(gameRoomId)
                .stream()
                .map(GameModelMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<GameRoomEntrance> findGameRoomEntranceByUserId(Long userId, List<GameRoomEntranceState> gameRoomEntranceStates) {
        return jpaRepository.findGameRoomEntranceByUserId(userId, gameRoomEntranceStates).map(GameModelMapper::toModel);
    }

    @Override
    public List<GameRoomEntrance> findGameRoomEntrancesByGameRoomIdAndState(Long gameRoomId, GameRoomEntranceState gameRoomEntranceState) {
        return jpaRepository.findGameRoomEntrancesByGameRoomIdAndState(gameRoomId, gameRoomEntranceState)
                .stream()
                .map(GameModelMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public long findGameRoomEntranceCountByGameRoomIdAndState(Long gameRoomId, GameRoomEntranceState gameRoomEntranceState) {
        return jpaRepository.findGameRoomEntranceCountByGameRoomIdAndState(gameRoomId, gameRoomEntranceState);
    }

    @Override
    public List<GameRoomEntrance> findGameRoomEntrancesByGameRoomIdAndStates(Long gameRoomId, List<GameRoomEntranceState> gameRoomEntranceStates) {
        return jpaRepository.findGameRoomEntrancesByGameRoomIdAndStates(gameRoomId, gameRoomEntranceStates)
                .stream()
                .map(GameModelMapper::toModel)
                .collect(Collectors.toList());
    }
}
