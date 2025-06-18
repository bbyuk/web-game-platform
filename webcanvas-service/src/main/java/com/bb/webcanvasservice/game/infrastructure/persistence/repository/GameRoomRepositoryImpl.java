package com.bb.webcanvasservice.game.infrastructure.persistence.repository;

import com.bb.webcanvasservice.game.domain.exception.GameRoomNotFoundException;
import com.bb.webcanvasservice.game.domain.exception.GameRoomParticipantNotFoundException;
import com.bb.webcanvasservice.game.domain.model.gameroom.*;
import com.bb.webcanvasservice.game.application.repository.GameRoomRepository;
import com.bb.webcanvasservice.game.infrastructure.persistence.entity.GameRoomJpaEntity;
import com.bb.webcanvasservice.game.infrastructure.persistence.entity.GameRoomParticipantJpaEntity;
import com.bb.webcanvasservice.game.infrastructure.persistence.entity.GameSessionJpaEntity;
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

    @Override
    public Optional<GameRoom> findGameRoomById(Long gameRoomId) {
        GameSessionJpaEntity gameSessionJpaEntity = gameSessionJpaRepository.findByGameRoomIdAndStates(gameRoomId, GameSessionState.active)
                .orElse(null);
        GameRoomJpaEntity gameRoomJpaEntity = gameSessionJpaEntity == null
                ? gameRoomJpaRepository.findById(gameRoomId).orElseThrow(GameRoomNotFoundException::new)
                : gameSessionJpaEntity.getGameRoomEntity();
        List<GameRoomParticipantJpaEntity> gameRoomParticipantEntities = gameRoomParticipantJpaRepository.findGameRoomParticipantsByGameRoomIdAndStates(gameRoomId, GameRoomParticipantState.joined);

        return Optional.of(
                GameModelMapper.toModel(
                        gameRoomJpaEntity,
                        gameSessionJpaEntity,
                        gameRoomParticipantEntities
                )
        );
    }

    @Override
    public Optional<GameRoom> findGameRoomByGameRoomParticipantId(Long gameRoomParticipantId) {
        GameRoomParticipantJpaEntity gameRoomParticipantEntity = gameRoomParticipantJpaRepository.findById(gameRoomParticipantId).orElseThrow(GameRoomParticipantNotFoundException::new);

        GameRoomJpaEntity gameRoomEntity = gameRoomParticipantEntity.getGameRoomEntity();
        GameSessionJpaEntity gameSessionJpaEntity = gameSessionJpaRepository.findByGameRoomIdAndStates(gameRoomEntity.getId(), GameSessionState.active).orElse(null);
        List<GameRoomParticipantJpaEntity> gameRoomParticipantEntities = gameRoomParticipantJpaRepository.findGameRoomParticipantsByGameRoomIdAndStates(gameRoomEntity.getId(), GameRoomParticipantState.joined);

        return Optional.of(
                GameModelMapper.toModel(
                        gameRoomEntity,
                        gameSessionJpaEntity,
                        gameRoomParticipantEntities
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


        return Optional.of(
                GameModelMapper.toModel(gameRoomEntity, gameSessionEntity, gameRoomParticipantEntities)
        );
    }

    @Override
    public boolean existsJoinCodeConflictOnActiveGameRoom(String joinCode) {
        return gameRoomJpaRepository.existsJoinCodeConflictOnActiveGameRoom(joinCode);
    }

    @Override
    public List<GameRoom> findGameRoomsByCapacityAndStateWithEntranceState(int gameRoomCapacity, List<GameRoomState> joinableStates, GameRoomParticipantState activeEntranceState) {
        List<GameRoomJpaEntity> gameRoomJpaEntities = gameRoomJpaRepository.findGameRoomsByCapacityAndStateWithEntranceState(gameRoomCapacity, joinableStates, activeEntranceState);
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
                .map(gameRoomJpaEntity -> (
                        GameModelMapper.toModel(
                                gameRoomJpaEntity,
                                gameSessionEntityPerGameRoomPerGameRoomId.get(gameRoomJpaEntity.getId()),
                                gameRoomParticipantEntitiesPerGameRoomId.get(gameRoomJpaEntity.getId())
                        )
                )).collect(Collectors.toList());
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

            Field currentGameSessionField = GameRoom.class.getDeclaredField("currentGameSession");
            Field participantsField = GameRoom.class.getDeclaredField("participants");

            currentGameSessionField.setAccessible(true);
            participantsField.setAccessible(true);

            GameSession gameSession = (GameSession) currentGameSessionField.get(gameRoom);

            GameSessionJpaEntity gameSessionEntity = gameSessionJpaRepository.save(
                    GameModelMapper.toEntity(gameSession, gameRoomEntity)
            );

            List<GameRoomParticipant> gameRoomParticipants = (List<GameRoomParticipant>) participantsField.get(gameRoom);

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


            return GameModelMapper.toModel(gameRoomEntity, gameSessionEntity, gameRoomParticipantEntities);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("저장 실패");
        }

    }

}
