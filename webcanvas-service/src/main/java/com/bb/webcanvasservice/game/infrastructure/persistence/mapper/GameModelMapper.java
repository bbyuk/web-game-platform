package com.bb.webcanvasservice.game.infrastructure.persistence.mapper;

import com.bb.webcanvasservice.game.domain.model.*;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoom;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameSession;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameTurn;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoomParticipant;
import com.bb.webcanvasservice.game.infrastructure.persistence.entity.*;
import com.bb.webcanvasservice.user.infrastructure.persistence.entity.UserJpaEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * entity <-> domain model
 *
 * @Game
 * @GameRoom
 * @GameRoomEntrance
 * @GameSession
 * @GameTurn
 * @GamePlayHistory
 */
public class GameModelMapper {

    public static GamePlayHistory toModel(GamePlayHistoryJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new GamePlayHistory(entity.getUserEntity().getId(), entity.getGameSessionEntity().getId());
    }

    public static GameRoom toModel(GameRoomJpaEntity gameRoomEntity, GameSessionJpaEntity gameSessionEntity, List<GameRoomParticipantJpaEntity> gameRoomParticipantEntities, List<GameTurnJpaEntity> gameTurns) {
        if (gameRoomEntity == null) {
            return null;
        }
        return new GameRoom(
                gameRoomEntity.getId(),
                gameRoomEntity.getJoinCode(),
                gameRoomEntity.getState(),
                gameRoomEntity.getCapacity(),
                toModel(gameSessionEntity, gameTurns),
                gameRoomParticipantEntities.stream().map(GameModelMapper::toModel).toList());
    }

    public static GameRoom toModel(GameRoomJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new GameRoom(entity.getId(), entity.getJoinCode(), entity.getState(), entity.getCapacity(), null, null);
    }


    public static GameRoomParticipant toModel(GameRoomParticipantJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new GameRoomParticipant(entity.getId(),
                entity.getGameRoomEntity().getId(),
                entity.getUserEntity().getId(),
                entity.getState(),
                entity.getNickname(),
                entity.getRole(),
                entity.isReady(),
                entity.getJoinedAt(),
                entity.getExitAt()
        );
    }


    public static GameSession toModel(GameSessionJpaEntity entity, List<GameTurnJpaEntity> gameTurns) {
        if (entity == null) {
            return null;
        }
        return new GameSession(
                entity.getId(),
                entity.getGameRoomEntity().getId(),
                entity.getTurnCount(),
                entity.getTimePerTurn(),
                entity.getState(),
                gameTurns.stream().map(GameModelMapper::toModel).toList()
        );
    }

    public static GameTurn toModel(GameTurnJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new GameTurn(
                entity.getId(),
                entity.getGameSessionEntity().getId(),
                entity.getDrawerId(),
                entity.getAnswer(),
                entity.getCreatedAt(),
                entity.getCorrectAnswererId(),
                entity.getState(),
                entity.getGameSessionEntity().getTimePerTurn()
        );
    }

    public static GameRoomJpaEntity toEntity(GameRoom gameRoom) {
        return new GameRoomJpaEntity(gameRoom.getId(), gameRoom.getJoinCode(), gameRoom.getState(), gameRoom.getCapacity());
    }

    public static GameRoomParticipantJpaEntity toEntity(GameRoomParticipant gameRoomParticipant, GameRoomJpaEntity gameRoomEntity, UserJpaEntity userEntity) {
        return new GameRoomParticipantJpaEntity(
                gameRoomParticipant.getId(),
                gameRoomEntity,
                userEntity,
                gameRoomParticipant.getNickname(),
                gameRoomParticipant.getRole(),
                gameRoomParticipant.getState(),
                gameRoomParticipant.isReady(),
                gameRoomParticipant.getJoinedAt(),
                gameRoomParticipant.getExitAt()
        );
    }

    public static GamePlayHistoryJpaEntity toEntity(GamePlayHistory gamePlayHistory, UserJpaEntity userEntity, GameSessionJpaEntity gameSessionEntity) {
        return new GamePlayHistoryJpaEntity(null, userEntity, gameSessionEntity);
    }

    public static GameSessionJpaEntity toEntity(GameSession gameSession, GameRoomJpaEntity gameRoomEntity) {
        return new GameSessionJpaEntity(gameSession.getId(), gameRoomEntity, gameSession.getState(), gameSession.getTurnCount(), gameSession.getTimePerTurn());
    }

    public static GameTurnJpaEntity toEntity(GameTurn gameTurn, GameSessionJpaEntity gameSessionEntity) {
        return new GameTurnJpaEntity(gameTurn.getId(), gameSessionEntity, gameTurn.getDrawerId(), gameTurn.getAnswer(), gameTurn.getCorrectAnswererId(), gameTurn.getState());
    }
}
