package com.bb.webcanvasservice.game.infrastructure.persistence.repository;

import com.bb.webcanvasservice.game.domain.exception.GameRoomNotFoundException;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoomParticipant;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoomParticipantState;
import com.bb.webcanvasservice.game.application.repository.GameRoomEntranceRepository;
import com.bb.webcanvasservice.game.infrastructure.persistence.entity.GameRoomParticipantJpaEntity;
import com.bb.webcanvasservice.user.domain.exception.UserNotFoundException;
import com.bb.webcanvasservice.game.infrastructure.persistence.mapper.GameModelMapper;
import com.bb.webcanvasservice.user.infrastructure.persistence.repository.UserJpaRepository;
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
    private final GameRoomParticipantJpaRepository gameRoomParticipantJpaRepository;
    private final GameRoomJpaRepository gameRoomJpaRepository;
    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<GameRoomParticipant> findById(Long gameRoomEntranceId) {
        return gameRoomParticipantJpaRepository.findById(gameRoomEntranceId).map(GameModelMapper::toModel);
    }

    @Override
    public GameRoomParticipant save(GameRoomParticipant gameRoomParticipant) {
        return GameModelMapper.toModel(
                gameRoomParticipantJpaRepository.save(
                        GameModelMapper.toEntity(
                                gameRoomParticipant,
                                gameRoomJpaRepository.findById(gameRoomParticipant.getGameRoomId()).orElseThrow(GameRoomNotFoundException::new),
                                userJpaRepository.findById(gameRoomParticipant.getUserId()).orElseThrow(UserNotFoundException::new)
                        )
                )
        );
    }

    @Override
    public boolean existsActiveEntrance(Long gameRoomId, Long userId) {
        String jpql = """
                select exists (
                     select 1
                     from   GameRoomParticipantJpaEntity gre
                     where  gre.gameRoomEntity.id = :gameRoomId
                     and    gre.userEntity.id = :userId
                     and    gre.state in :enteredStates
                )
                """;

        return em.createQuery(jpql, Boolean.class)
                .setParameter("enteredStates", GameRoomParticipantState.entered)
                .setParameter("gameRoomId", gameRoomId)
                .setParameter("userId", userId)
                .getSingleResult();
    }

    @Override
    public int findEnteredUserCount(Long gameRoomId) {

        String jpql = """
                    select  count(gre)
                    from    GameRoomParticipantJpaEntity gre
                    where   gre.gameRoomEntity.id = :gameRoomId
                    and     gre.state in :enteredStates
                """;
        return em.createQuery(jpql, Long.class)
                .setParameter("gameRoomId", gameRoomId)
                .setParameter("enteredStates", GameRoomParticipantState.entered)
                .getSingleResult().intValue();
    }

    @Override
    public boolean existsGameRoomEntranceByUserId(Long userId) {
        return gameRoomParticipantJpaRepository.existsGameRoomEntranceByUserId(userId);
    }

    @Override
    public Optional<GameRoomParticipant> findByUserId(Long userId) {
        return gameRoomParticipantJpaRepository.findByUserId(userId)
                .map(GameModelMapper::toModel);
    }

    @Override
    public List<GameRoomParticipant> findGameRoomEntrancesByGameRoomIdWithLock(Long gameRoomId) {
        return gameRoomParticipantJpaRepository.findGameRoomEntrancesByGameRoomIdWithLock(gameRoomId)
                .stream()
                .map(GameModelMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<GameRoomParticipant> findCurrentEnteredGameRoomEntranceByUserId(Long userId) {
        String jpql = """
                select  gre
                from    GameRoomParticipantJpaEntity gre
                where   gre.userEntity.id = :userId
                and     gre.state in :enteredStates
                """;

        GameRoomParticipantJpaEntity gameRoomParticipantJpaEntity = em.createQuery(jpql, GameRoomParticipantJpaEntity.class)
                .setParameter("userId", userId)
                .setParameter("enteredStates", GameRoomParticipantState.entered)
                .getSingleResult();

        return Optional.of(GameModelMapper.toModel(gameRoomParticipantJpaEntity));
    }

    @Override
    public List<GameRoomParticipant> findGameRoomEntrancesByGameRoomIdAndState(Long gameRoomId, GameRoomParticipantState gameRoomParticipantState) {
        return gameRoomParticipantJpaRepository.findGameRoomEntrancesByGameRoomIdAndState(gameRoomId, gameRoomParticipantState)
                .stream()
                .map(GameModelMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public long findGameRoomEntranceCountByGameRoomIdAndState(Long gameRoomId, GameRoomParticipantState gameRoomParticipantState) {
        return gameRoomParticipantJpaRepository.findGameRoomEntranceCountByGameRoomIdAndState(gameRoomId, gameRoomParticipantState);
    }

    @Override
    public List<GameRoomParticipant> findGameRoomEntrancesByGameRoomIdAndStates(Long gameRoomId, List<GameRoomParticipantState> gameRoomParticipantStates) {
        return gameRoomParticipantJpaRepository.findGameRoomEntrancesByGameRoomIdAndStates(gameRoomId, gameRoomParticipantStates)
                .stream()
                .map(GameModelMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void updateGameRoomEntrancesState(List<Long> gameRoomEntranceIds, GameRoomParticipantState state) {
        String jpql = """
                update  GameRoomParticipantJpaEntity gre
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
    public List<GameRoomParticipant> findGameRoomEntrancesByIds(List<Long> gameRoomEntranceIds) {
        String jpql = """
                select  gre
                from    GameRoomParticipantJpaEntity gre
                where   gre.id in :ids
                """;
        return em.createQuery(jpql, GameRoomParticipantJpaEntity.class)
                .setParameter("ids", gameRoomEntranceIds)
                .getResultStream()
                .map(GameModelMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void saveAll(List<GameRoomParticipant> gameRoomParticipants) {
        gameRoomParticipantJpaRepository.saveAll(
                gameRoomParticipants.stream().map(gameRoomEntrance -> GameModelMapper.toEntity(
                                gameRoomEntrance,
                                gameRoomJpaRepository.findById(gameRoomEntrance.getGameRoomId()).orElseThrow(GameRoomNotFoundException::new),
                                userJpaRepository.findById(gameRoomEntrance.getUserId()).orElseThrow(UserNotFoundException::new)
                        ))
                        .collect(Collectors.toList())
        );
    }
}
