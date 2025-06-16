package com.bb.webcanvasservice.infrastructure.persistence.game.repository;

import com.bb.webcanvasservice.domain.game.exception.GameRoomNotFoundException;
import com.bb.webcanvasservice.domain.game.model.GameRoomEntrance;
import com.bb.webcanvasservice.domain.game.model.GameRoomEntranceState;
import com.bb.webcanvasservice.domain.game.repository.GameRoomEntranceRepository;
import com.bb.webcanvasservice.domain.user.exception.UserNotFoundException;
import com.bb.webcanvasservice.infrastructure.persistence.game.GameModelMapper;
import com.bb.webcanvasservice.infrastructure.persistence.game.entity.GameRoomEntranceJpaEntity;
import com.bb.webcanvasservice.infrastructure.persistence.user.repository.UserJpaRepository;
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
    private final GameRoomEntranceJpaRepository gameRoomEntranceJpaRepository;
    private final GameRoomJpaRepository gameRoomJpaRepository;
    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<GameRoomEntrance> findById(Long gameRoomEntranceId) {
        return gameRoomEntranceJpaRepository.findById(gameRoomEntranceId).map(GameModelMapper::toModel);
    }

    @Override
    public GameRoomEntrance save(GameRoomEntrance gameRoomEntrance) {
        return GameModelMapper.toModel(
                gameRoomEntranceJpaRepository.save(
                        GameModelMapper.toEntity(
                                gameRoomEntrance,
                                gameRoomJpaRepository.findById(gameRoomEntrance.getGameRoomId()).orElseThrow(GameRoomNotFoundException::new),
                                userJpaRepository.findById(gameRoomEntrance.getUserId()).orElseThrow(UserNotFoundException::new)
                        )
                )
        );
    }

    @Override
    public boolean existsActiveEntrance(Long gameRoomId, Long userId) {
        String jpql = """
                select exists (
                     select 1
                     from   GameRoomEntranceJpaEntity gre
                     where  gre.gameRoomEntity.id = :gameRoomId
                     and    gre.userEntity.id = :userId
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
                    where   gre.gameRoomEntity.id = :gameRoomId
                    and     gre.state in :enteredStates
                """;
        return em.createQuery(jpql, Long.class)
                .setParameter("gameRoomId", gameRoomId)
                .setParameter("enteredStates", GameRoomEntranceState.entered)
                .getSingleResult().intValue();
    }

    @Override
    public boolean existsGameRoomEntranceByUserId(Long userId) {
        return gameRoomEntranceJpaRepository.existsGameRoomEntranceByUserId(userId);
    }

    @Override
    public Optional<GameRoomEntrance> findByUserId(Long userId) {
        return gameRoomEntranceJpaRepository.findByUserId(userId)
                .map(GameModelMapper::toModel);
    }

    @Override
    public List<GameRoomEntrance> findGameRoomEntrancesByGameRoomIdWithLock(Long gameRoomId) {
        return gameRoomEntranceJpaRepository.findGameRoomEntrancesByGameRoomIdWithLock(gameRoomId)
                .stream()
                .map(GameModelMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<GameRoomEntrance> findCurrentEnteredGameRoomEntranceByUserId(Long userId) {
        String jpql = """
                select  gre
                from    GameRoomEntranceJpaEntity gre
                where   gre.userEntity.id = :userId
                and     gre.state in :enteredStates
                """;

        GameRoomEntranceJpaEntity gameRoomEntranceJpaEntity = em.createQuery(jpql, GameRoomEntranceJpaEntity.class)
                .setParameter("userId", userId)
                .setParameter("enteredStates", GameRoomEntranceState.entered)
                .getSingleResult();

        return Optional.of(GameModelMapper.toModel(gameRoomEntranceJpaEntity));
    }

    @Override
    public List<GameRoomEntrance> findGameRoomEntrancesByGameRoomIdAndState(Long gameRoomId, GameRoomEntranceState gameRoomEntranceState) {
        return gameRoomEntranceJpaRepository.findGameRoomEntrancesByGameRoomIdAndState(gameRoomId, gameRoomEntranceState)
                .stream()
                .map(GameModelMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public long findGameRoomEntranceCountByGameRoomIdAndState(Long gameRoomId, GameRoomEntranceState gameRoomEntranceState) {
        return gameRoomEntranceJpaRepository.findGameRoomEntranceCountByGameRoomIdAndState(gameRoomId, gameRoomEntranceState);
    }

    @Override
    public List<GameRoomEntrance> findGameRoomEntrancesByGameRoomIdAndStates(Long gameRoomId, List<GameRoomEntranceState> gameRoomEntranceStates) {
        return gameRoomEntranceJpaRepository.findGameRoomEntrancesByGameRoomIdAndStates(gameRoomId, gameRoomEntranceStates)
                .stream()
                .map(GameModelMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void updateGameRoomEntrancesState(List<Long> gameRoomEntranceIds, GameRoomEntranceState state) {
        String jpql = """
                update  GameRoomEntranceJpaEntity gre
                set     gre.state = :state
                where   gre.id in :ids
                """;

        em.createQuery(jpql)
                .setParameter("state", state)
                .setParameter("ids", gameRoomEntranceIds)
                .executeUpdate();

        em.clear();
    }

    @Override
    public List<GameRoomEntrance> findGameRoomEntrancesByIds(List<Long> gameRoomEntranceIds) {
        String jpql = """
                select  gre
                from    GameRoomEntranceJpaEntity gre
                where   gre.id in :ids
                """;
        return em.createQuery(jpql, GameRoomEntranceJpaEntity.class)
                .setParameter("ids", gameRoomEntranceIds)
                .getResultStream()
                .map(GameModelMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void saveAll(List<GameRoomEntrance> gameRoomEntrances) {
        gameRoomEntranceJpaRepository.saveAll(
                gameRoomEntrances.stream().map(gameRoomEntrance -> GameModelMapper.toEntity(
                                gameRoomEntrance,
                                gameRoomJpaRepository.findById(gameRoomEntrance.getGameRoomId()).orElseThrow(GameRoomNotFoundException::new),
                                userJpaRepository.findById(gameRoomEntrance.getUserId()).orElseThrow(UserNotFoundException::new)
                        ))
                        .collect(Collectors.toList())
        );
    }
}
