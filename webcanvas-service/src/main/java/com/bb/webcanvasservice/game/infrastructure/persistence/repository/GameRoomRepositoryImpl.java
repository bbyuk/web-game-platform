package com.bb.webcanvasservice.game.infrastructure.persistence.repository;

import com.bb.webcanvasservice.game.domain.exception.GameRoomNotFoundException;
import com.bb.webcanvasservice.game.domain.exception.GameRoomParticipantNotFoundException;
import com.bb.webcanvasservice.game.domain.model.gameroom.*;
import com.bb.webcanvasservice.game.application.repository.GameRoomRepository;
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

import java.lang.reflect.Field;
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
        GameSessionJpaEntity gameSessionJpaEntity = gameSessionJpaRepository.findByGameRoomIdAndStates(gameRoomId, GameSessionState.active)
                .orElse(null);

        GameRoomJpaEntity gameRoomJpaEntity = gameSessionJpaEntity == null
                ? gameRoomJpaRepository.findById(gameRoomId).orElseThrow(GameRoomNotFoundException::new)
                : gameSessionJpaEntity.getGameRoomEntity();
        
        List<GameRoomParticipantJpaEntity> gameRoomParticipantEntities = gameRoomParticipantJpaRepository.findGameRoomParticipantsByGameRoomIdAndStates(gameRoomId, GameRoomParticipantState.joined);
        List<GameTurnJpaEntity> gameTurnEntities = gameSessionJpaEntity == null
                ? null
                : gameTurnJpaRepository.findTurnsByGameSessionId(gameSessionJpaEntity.getId());

        return Optional.of(
                GameModelMapper.toModel(
                        gameRoomJpaEntity,
                        gameSessionJpaEntity,
                        gameRoomParticipantEntities,
                        gameTurnEntities
                )
        );
    }

    @Override
    public Optional<GameRoom> findGameRoomByGameRoomParticipantId(Long gameRoomParticipantId) {
        GameRoomParticipantJpaEntity gameRoomParticipantEntity = gameRoomParticipantJpaRepository.findById(gameRoomParticipantId).orElseThrow(GameRoomParticipantNotFoundException::new);

        GameRoomJpaEntity gameRoomEntity = gameRoomParticipantEntity.getGameRoomEntity();
        GameSessionJpaEntity gameSessionJpaEntity = gameSessionJpaRepository.findByGameRoomIdAndStates(gameRoomEntity.getId(), GameSessionState.active).orElse(null);
        List<GameRoomParticipantJpaEntity> gameRoomParticipantEntities = gameRoomParticipantJpaRepository.findGameRoomParticipantsByGameRoomIdAndStates(gameRoomEntity.getId(), GameRoomParticipantState.joined);
        List<GameTurnJpaEntity> gameTurnEntities = gameTurnJpaRepository.findTurnsByGameSessionId(gameSessionJpaEntity.getId());

        return Optional.of(
                GameModelMapper.toModel(
                        gameRoomEntity,
                        gameSessionJpaEntity,
                        gameRoomParticipantEntities,
                        gameTurnEntities
                )
        );
    }

    @Override
    public Optional<GameRoom> findGameRoomByGameSessionId(Long gameSessionId) {
        return gameRoomJpaRepository.findByGameSessionId(gameSessionId);
    }

    @Override
    public Optional<GameRoom> findCurrentJoinedGameRoomByUserId(Long userId) {
        GameRoomParticipantJpaEntity gameRoomParticipantJpaEntity = gameRoomParticipantJpaRepository.findGameRoomEntranceByUserIdAndGameRoomStates(userId, GameRoomParticipantState.joined)
                .orElseThrow(GameRoomParticipantNotFoundException::new);
        GameRoomJpaEntity gameRoomEntity = gameRoomParticipantJpaEntity.getGameRoomEntity();
        GameSessionJpaEntity gameSessionEntity = gameSessionJpaRepository.findByGameRoomIdAndStates(gameRoomEntity.getId(), GameSessionState.active).orElse(null);
        List<GameRoomParticipantJpaEntity> gameRoomParticipantEntities = gameRoomParticipantJpaRepository.findGameRoomParticipantsByGameRoomIdAndStates(gameRoomEntity.getId(), GameRoomParticipantState.joined);
        List<GameTurnJpaEntity> gameTurnEntities = gameTurnJpaRepository.findTurnsByGameSessionId(gameSessionEntity.getId());


        return Optional.of(
                GameModelMapper.toModel(gameRoomEntity, gameSessionEntity, gameRoomParticipantEntities, gameTurnEntities)
        );
    }

    @Override
    public boolean existsJoinCodeConflictOnActiveGameRoom(String joinCode) {
        return gameRoomJpaRepository.existsJoinCodeConflictOnActiveGameRoom(joinCode);
    }

    @Override
    public List<GameRoom> findGameRoomsByCapacityAndGameRoomStateAndGameRoomParticipantState(int gameRoomCapacity, GameRoomState gameRoomState, GameRoomParticipantState gameRoomParticipantState) {
        List<GameRoomJpaEntity> gameRoomJpaEntities = gameRoomJpaRepository.findGameRoomsByCapacityAndStateWithEntranceState(gameRoomCapacity, gameRoomState, gameRoomParticipantState);
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
    public Optional<GameRoom> findRoomWithJoinCodeForEnter(String joinCode) {
        return gameRoomJpaRepository.findRoomWithJoinCodeForEnter(joinCode)
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
