package com.bb.webcanvasservice.game.infrastructure.persistence.repository;

import com.bb.webcanvasservice.game.domain.repository.GameRoomRepository;
import com.bb.webcanvasservice.game.domain.model.room.*;
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

import java.util.Collections;
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
    public List<GameRoom> findGameRoomsByCapacityAndGameRoomStateAndGameRoomParticipantState(GameRoomState gameRoomState, GameRoomParticipantState gameRoomParticipantState) {
        List<GameRoomJpaEntity> gameRoomJpaEntities = gameRoomJpaRepository.findGameRoomsByCapacityAndStateAndGameRoomParticipantState(
                gameRoomState,
                gameRoomParticipantState);

        List<GameRoomParticipantJpaEntity> gameRoomParticipantJpaEntities =
                gameRoomParticipantJpaRepository.findGameRoomParticipantsByGameRoomParticipantStateAndGameRoomState(
                        gameRoomJpaEntities.stream().map(GameRoomJpaEntity::getId).collect(Collectors.toList()),
                        gameRoomParticipantState,
                        gameRoomState);

        Map<Long, List<GameRoomParticipantJpaEntity>> gameRoomPaticipantMap = gameRoomParticipantJpaEntities.stream()
                .collect(Collectors.groupingBy(participantJpaEntity -> participantJpaEntity.getGameRoomEntity().getId()));

        return gameRoomJpaEntities
                .stream()
                .map(gameRoomJpaEntity -> {
                    List<GameRoomParticipantJpaEntity> gameRoomParticipantEntities = gameRoomPaticipantMap.get(gameRoomJpaEntity.getId());
                    return (
                            GameModelMapper.toModel(
                                    gameRoomJpaEntity,
                                    null,
                                    gameRoomParticipantEntities,
                                    null
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
            GameRoomJpaEntity gameRoomJpaEntity = gameRoomJpaRepository.save(
                    GameModelMapper.toEntity(gameRoom)
            );

            List<GameRoomParticipantJpaEntity> gameRoomParticipantJpaEntities = gameRoom.getParticipants().stream()
                    .map(gameRoomParticipant
                            -> GameModelMapper.toEntity(
                            gameRoomParticipant,
                            gameRoomJpaEntity,
                            userJpaRepository.findById(gameRoomParticipant.getUserId()).orElseThrow(UserNotFoundException::new))
                    )
                    .toList();
            gameRoomParticipantJpaRepository.saveAll(gameRoomParticipantJpaEntities);

            Optional<GameSession> optionalGameSession = Optional.ofNullable(gameRoom.getGameSession());

            Optional<GameSessionJpaEntity> savedGameSessionJpaEntity =
                    optionalGameSession
                            .map(gameSession -> GameModelMapper.toEntity(gameSession, gameRoomJpaEntity))
                            .map(gameSessionJpaRepository::save);

            List<GameTurnJpaEntity> gameTurnJpaEntities = optionalGameSession
                    .map(GameSession::getGameTurns)
                    .orElseGet(Collections::emptyList)
                    .stream()
                    .map(gameTurn -> GameModelMapper.toEntity(
                            gameTurn,
                            savedGameSessionJpaEntity.orElse(null)
                    ))
                    .collect(Collectors.toList());

            if (!gameTurnJpaEntities.isEmpty()) {
                gameTurnJpaRepository.saveAll(gameTurnJpaEntities);
            }


            return GameModelMapper.toModel(
                    gameRoomJpaEntity,
                    savedGameSessionJpaEntity.orElse(null),
                    gameRoomParticipantJpaEntities,
                    gameTurnJpaEntities
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("저장 실패");
        }
    }

}
