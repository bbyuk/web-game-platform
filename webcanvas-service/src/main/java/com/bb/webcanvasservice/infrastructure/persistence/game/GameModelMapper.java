package com.bb.webcanvasservice.infrastructure.persistence.game;

import com.bb.webcanvasservice.domain.game.model.*;
import com.bb.webcanvasservice.infrastructure.persistence.game.entity.*;

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
        return new GamePlayHistory(entity.getUserId(), entity.getGameSessionId());
    }

    public static GameRoom toModel(GameRoomJpaEntity entity) {
        return new GameRoom(entity.getId(), entity.getJoinCode(), entity.getState());
    }

    public static GameRoomEntrance toModel(GameRoomEntranceJpaEntity entity) {
        return new GameRoomEntrance(entity.getId(),
                entity.getGameRoomId(),
                entity.getUserId(),
                entity.getState(),
                entity.getNickname(),
                entity.getRole(),
                entity.isReady());
    }


    public static GameSession toModel(GameSessionJpaEntity entity) {
        return new GameSession(
                entity.getId(),
                entity.getGameRoomId(),
                entity.getTurnCount(),
                entity.getTimePerTurn(),
                entity.getState()
        );
    }

    public static GameTurn toModel(GameTurnJpaEntity entity) {
        return new GameTurn(
                entity.getId(),
                entity.getGameSessionId(),
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

    public static GameRoomEntranceJpaEntity toEntity(GameRoomEntrance gameRoomEntrance) {
        return new GameRoomEntranceJpaEntity(
                gameRoomEntrance.getGameRoomId(),
                gameRoomEntrance.getUserId(),
                gameRoomEntrance.getState(),
                gameRoomEntrance.getNickname(),
                gameRoomEntrance.getRole());
    }

    public static GameSessionJpaEntity toEntity(GameSession newGameSession) {
        return new GameSessionJpaEntity(newGameSession.getGameRoomId(), newGameSession.getTurnCount(), newGameSession.getTimePerTurn());
    }

    public static GamePlayHistoryJpaEntity toEntity(GamePlayHistory gamePlayHistory) {
        return new GamePlayHistoryJpaEntity(gamePlayHistory.getUserId(), gamePlayHistory.getGameSessionId());
    }

    public static GameTurnJpaEntity toEntity(GameTurn gameTurn) {
        return new GameTurnJpaEntity(gameTurn.getGameSessionId(), gameTurn.getDrawerId(), gameTurn.getAnswer());
    }
}
