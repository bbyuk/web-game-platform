package com.bb.webcanvasservice.game.infrastructure.persistence.repository;

import com.bb.webcanvasservice.game.application.repository.GameRoomRepository;
import com.bb.webcanvasservice.game.domain.model.gameroom.*;
import com.bb.webcanvasservice.game.infrastructure.persistence.entity.GameRoomJpaEntity;
import com.bb.webcanvasservice.game.infrastructure.persistence.entity.GameRoomParticipantJpaEntity;
import com.bb.webcanvasservice.game.infrastructure.persistence.entity.GameSessionJpaEntity;
import com.bb.webcanvasservice.game.infrastructure.persistence.entity.GameTurnJpaEntity;
import com.bb.webcanvasservice.game.infrastructure.persistence.mapper.GameModelMapper;
import com.bb.webcanvasservice.user.domain.exception.UserNotFoundException;
import com.bb.webcanvasservice.user.infrastructure.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class GameRoomRepositoryImpl implements GameRoomRepository {
    private final GameRoomJpaRepository gameRoomJpaRepository;
    private final GameSessionJpaRepository gameSessionJpaRepository;
    private final GameRoomParticipantJpaRepository gameRoomParticipantJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final GameTurnJpaRepository gameTurnJpaRepository;

    @Override
    public Optional<GameRoom> findGameRoomById(Long gameRoomId) {
        return gameRoomJpaRepository.findById(gameRoomId)
                .map(gameRoomJpaEntity -> {
                            List<GameRoomParticipantJpaEntity> gameRoomParticipantJpaEntities = gameRoomParticipantJpaRepository.findGameRoomParticipantsByGameRoomIdAndStates(
                                    gameRoomId, GameRoomParticipantState.joined
                            );
                            return gameSessionJpaRepository.findByGameRoomIdAndStates(gameRoomId, GameSessionState.active)
                                    .map(gameSessionJpaEntity ->
                                            GameModelMapper.toModel(
                                                    gameRoomJpaEntity,
                                                    gameSessionJpaEntity,
                                                    gameRoomParticipantJpaEntities,
                                                    gameTurnJpaRepository.findTurnsByGameSessionId(gameSessionJpaEntity.getId())
                                            )
                                    )
                                    .orElse(GameModelMapper.toModel(
                                            gameRoomJpaEntity,
                                            null,
                                            gameRoomParticipantJpaEntities,
                                            null
                                    ));
                        }
                );
    }

    @Override
    public Optional<GameRoom> findGameRoomByGameRoomParticipantId(Long gameRoomParticipantId) {
        return gameRoomParticipantJpaRepository.findById(gameRoomParticipantId)
                .flatMap(gameRoomParticipantJpaEntity ->
                        findGameRoomById(
                                gameRoomParticipantJpaEntity
                                        .getGameRoomEntity()
                                        .getId()
                        )
                );
    }

    @Override
    public Optional<GameRoom> findGameRoomByGameSessionId(Long gameSessionId) {
        return gameRoomJpaRepository.findByGameSessionId(gameSessionId)
                .flatMap(gameRoomJpaEntity -> findGameRoomById(gameRoomJpaEntity.getId()));
    }

    @Override
    public Optional<GameRoom> findCurrentJoinedGameRoomByUserId(Long userId) {
        return gameRoomParticipantJpaRepository.findGameRoomParticipantByUserIdAndGameRoomStates(userId, GameRoomParticipantState.joined)
                .flatMap(participantJpaEntity -> findGameRoomById(participantJpaEntity.getGameRoomEntity().getId()));
    }

    @Override
    public boolean existsJoinCodeConflictOnActiveGameRoom(String joinCode) {
        return !gameRoomJpaRepository
                .findGameRoomByJoinCodeAndActiveStatesWithLock(
                        joinCode,
                        GameRoomState.active)
                .isEmpty();
    }

    @Override
    public List<GameRoom> findGameRoomsByCapacityAndGameRoomStateAndGameRoomParticipantState(int gameRoomCapacity, GameRoomState gameRoomState, GameRoomParticipantState gameRoomParticipantState) {
        List<GameRoomJpaEntity> gameRoomJpaEntities = gameRoomJpaRepository.findGameRoomsByCapacityAndStateAndGameRoomParticipantState(gameRoomCapacity, gameRoomState, gameRoomParticipantState);
        Map<Long, GameSessionJpaEntity> gameSessionEntityPerGameRoomPerGameRoomId = gameSessionJpaRepository.findGameSessionsByGameRoomsAndStates(gameRoomJpaEntities, GameSessionState.active)
                .stream()
                .collect(Collectors.toMap(
                        entity -> entity.getGameRoomEntity().getId(),
                        entity -> entity
                ));
        Map<Long, List<GameRoomParticipantJpaEntity>> gameRoomParticipantEntitiesPerGameRoomId = gameRoomParticipantJpaRepository.findGameRoomParticipantsByGameRooms(gameRoomJpaEntities)
                .stream()
                .collect(Collectors.groupingBy(gameRoomParticipantJpaEntity -> gameRoomParticipantJpaEntity.getGameRoomEntity().getId()));

        return gameRoomJpaEntities
                .stream()
                .map(gameRoomJpaEntity -> {
                    GameSessionJpaEntity gameSessionEntity = gameSessionEntityPerGameRoomPerGameRoomId.get(gameRoomJpaEntity.getId());
                    List<GameRoomParticipantJpaEntity> gameRoomParticipantEntities = gameRoomParticipantEntitiesPerGameRoomId.get(gameRoomJpaEntity.getId());
                    return (
                            GameModelMapper.toModel(
                                    gameRoomJpaEntity,
                                    gameSessionEntity,
                                    gameRoomParticipantEntities,
                                    gameTurnJpaRepository.findTurnsByGameSessionId(gameSessionEntity.getId())
                            )
                    );
                }).collect(Collectors.toList());
    }

    @Override
    public Optional<GameRoom> findGameRoomByJoinCodeAndState(String joinCode, GameRoomState state) {
        return gameRoomJpaRepository.findGameRoomByJoinCodeAndState(joinCode, state)
                .map(GameModelMapper::toModel);
    }

    @Override
    public GameRoom save(GameRoom gameRoom) {
        try {
            GameRoomJpaEntity gameRoomEntity = gameRoomJpaRepository.save(
                    GameModelMapper.toEntity(gameRoom)
            );


            GameSession gameSession = gameRoom.getGameSession();
            GameSessionJpaEntity gameSessionEntity =
                    gameSession == null
                            ? null
                            : gameSessionJpaRepository.save(GameModelMapper.toEntity(gameSession, gameRoomEntity));


            List<GameRoomParticipant> gameRoomParticipants = gameRoom.getParticipants();
            /**
             * TODO N+1 문제 체크
             */
            List<GameRoomParticipantJpaEntity> gameRoomParticipantEntities = gameRoomParticipants
                    .stream()
                    .map(gameRoomParticipant
                            -> GameModelMapper.toEntity(
                            gameRoomParticipant,
                            gameRoomEntity,
                            userJpaRepository.findById(gameRoomParticipant.getUserId()).orElseThrow(UserNotFoundException::new))
                    )
                    .toList();
            gameRoomParticipantJpaRepository.saveAll(
                    gameRoomParticipantEntities
            );

            List<GameTurnJpaEntity> gameTurnEntities = gameSessionEntity == null
                    ? null
                    : gameTurnJpaRepository.findTurnsByGameSessionId(gameSessionEntity.getId());

            return GameModelMapper.toModel(
                    gameRoomEntity,
                    gameSessionEntity,
                    gameRoomParticipantEntities,
                    gameTurnEntities
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("저장 실패");
        }
    }

}
