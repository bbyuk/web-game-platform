package com.bb.webcanvasservice.game.infrastructure.persistence.mapper;

import com.bb.webcanvasservice.game.domain.model.*;
import com.bb.webcanvasservice.game.domain.model.room.GameRoom;
import com.bb.webcanvasservice.game.domain.model.session.GamePlayer;
import com.bb.webcanvasservice.game.domain.model.session.GameSession;
import com.bb.webcanvasservice.game.domain.model.session.GameTurn;
import com.bb.webcanvasservice.game.domain.model.room.GameRoomParticipant;
import com.bb.webcanvasservice.game.infrastructure.persistence.entity.*;
import com.bb.webcanvasservice.user.infrastructure.persistence.entity.UserJpaEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * entity <-> domain model
 *
 * @Game
 * @GameRoom
 * @GameRoomParticipant
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

    public static GameRoom toModel(GameRoomJpaEntity gameRoomEntity, List<GameRoomParticipantJpaEntity> gameRoomParticipantEntities) {
        if (gameRoomEntity == null) {
            return null;
        }
        return new GameRoom(
                gameRoomEntity.getId(),
                gameRoomEntity.getJoinCode(),
                gameRoomEntity.getState(),
                gameRoomEntity.getCapacity(),
                gameRoomParticipantEntities.stream().map(GameModelMapper::toModel).collect(Collectors.toList()));
    }

    public static GameRoom toModel(GameRoomJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new GameRoom(entity.getId(), entity.getJoinCode(), entity.getState(), entity.getCapacity(), new ArrayList<>());
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


    public static GameSession toModel(GameSessionJpaEntity gameSessionJpaEntity, List<GamePlayerJpaEntity> gamePlayerJpaEntities, List<GameTurnJpaEntity> gameTurnJpaEntities) {
        if (gameSessionJpaEntity == null) {
            return null;
        }
        return new GameSession(
                gameSessionJpaEntity.getId(),
                gameSessionJpaEntity.getGameRoomEntity().getId(),
                gameSessionJpaEntity.getTurnCount(),
                gameSessionJpaEntity.getTimePerTurn(),
                gameSessionJpaEntity.getState(),
                gamePlayerJpaEntities.stream().map(GameModelMapper::toModel).collect(Collectors.toList()),
                gameTurnJpaEntities.stream().map(GameModelMapper::toModel).collect(Collectors.toList())
        );
    }

    private static GamePlayer toModel(GamePlayerJpaEntity gamePlayerJpaEntity) {
        if (gamePlayerJpaEntity == null) {
            return null;
        }
        return new GamePlayer(
                gamePlayerJpaEntity.getId(),
                gamePlayerJpaEntity.getGameSessionEntity().getId(),
                gamePlayerJpaEntity.getUserEntity().getId(),
                gamePlayerJpaEntity.getNickname(),
                gamePlayerJpaEntity.getState()
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
        return new GameSessionJpaEntity(gameSession.id(), gameRoomEntity, gameSession.state(), gameSession.turnCount(), gameSession.timePerTurn());
    }

    public static GameTurnJpaEntity toEntity(GameTurn gameTurn, GameSessionJpaEntity gameSessionEntity) {
        return new GameTurnJpaEntity(gameTurn.id(), gameSessionEntity, gameTurn.drawerId(), gameTurn.answer(), gameTurn.correctAnswererId(), gameTurn.state());
    }

    public static GamePlayerJpaEntity toEntity(GamePlayer gamePlayer, GameSessionJpaEntity gameSessionJpaEntity, UserJpaEntity userJpaEntity) {
        if (gamePlayer == null) {
            return null;
        }

        return new GamePlayerJpaEntity(
                gamePlayer.id(),
                gameSessionJpaEntity,
                userJpaEntity,
                gamePlayer.state(),
                gamePlayer.nickname()
        );
    }

}
