package com.bb.webcanvasservice.game.infrastructure.persistence.mapper;

import com.bb.webcanvasservice.game.domain.model.*;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoom;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameSession;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameTurn;
import com.bb.webcanvasservice.game.domain.model.participant.GameRoomParticipant;
import com.bb.webcanvasservice.game.infrastructure.persistence.entity.*;
import com.bb.webcanvasservice.user.infrastructure.persistence.entity.UserJpaEntity;

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
        return new GamePlayHistory(entity.getUserEntity().getId(), entity.getGameSessionEntity().getId());
    }

    public static GameRoom toModel(GameRoomJpaEntity entity) {
        return new GameRoom(entity.getId(), entity.getJoinCode(), entity.getState());
    }

    public static GameRoomParticipant toModel(GameRoomParticipantJpaEntity entity) {
        return new GameRoomParticipant(entity.getId(),
                entity.getGameRoomEntity().getId(),
                entity.getUserEntity().getId(),
                entity.getState(),
                entity.getNickname(),
                entity.getRole(),
                entity.isReady());
    }


    public static GameSession toModel(GameSessionJpaEntity entity) {
        return new GameSession(
                entity.getId(),
                entity.getGameRoomEntity().getId(),
                entity.getTurnCount(),
                entity.getTimePerTurn(),
                entity.getState()
        );
    }

    public static GameTurn toModel(GameTurnJpaEntity entity) {
        return new GameTurn(
                entity.getId(),
                entity.getGameSessionEntity().getId(),
                entity.getDrawerId(),
                entity.getAnswer(),
                entity.getCreatedAt(),
                entity.getCorrectAnswererId(),
                entity.getState()
        );
    }

    public static GameRoomJpaEntity toEntity(GameRoom gameRoom) {
        return new GameRoomJpaEntity(gameRoom.getId(), gameRoom.getJoinCode(), gameRoom.getState());
    }

    public static GameRoomParticipantJpaEntity toEntity(GameRoomParticipant gameRoomParticipant, GameRoomJpaEntity gameRoomEntity, UserJpaEntity userEntity) {
        return new GameRoomParticipantJpaEntity(
                gameRoomParticipant.getId(),
                gameRoomEntity,
                userEntity,
                gameRoomParticipant.getNickname(),
                gameRoomParticipant.getRole(),
                gameRoomParticipant.getState(),
                gameRoomParticipant.isReady()
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
