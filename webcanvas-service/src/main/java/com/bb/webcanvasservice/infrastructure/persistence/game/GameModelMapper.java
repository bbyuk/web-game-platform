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
        return new GamePlayHistory(entity.getUser().getId(), entity.getGameSession().getId());
    }

    public static GameRoom toModel(GameRoomJpaEntity entity) {
        return new GameRoom(entity.getId(), entity.getJoinCode(), entity.getState());
    }

    public static GameRoomEntrance toModel(GameRoomEntranceJpaEntity entity) {
        return new GameRoomEntrance(entity.getId(),
                toModel(entity.getGameRoom()),
                entity.getUser().getId(),
                entity.getState(),
                entity.getNickname(),
                entity.getRole(),
                entity.isReady());
    }


    public static GameSession toModel(GameSessionJpaEntity entity) {
        return new GameSession(
                entity.getId(),
                toModel(entity.getGameRoom()),
                entity.getTurnCount(),
                entity.getTimePerTurn(),
                entity.getState()
        );
    }

    public static GameTurn toModel(GameTurnJpaEntity entity) {
        return new GameTurn(
                entity.getId(),
                toModel(entity.getGameSession()),
                entity.getDrawer().getId(),
                entity.getAnswer(),
                entity.getCreatedAt(),
                entity.getCorrectAnswerer() == null ? null : entity.getCorrectAnswerer().getId(),
                entity.getState()
        );
    }

    public static GameRoomJpaEntity toEntity(GameRoom gameRoom) {
        return new GameRoomJpaEntity(gameRoom.getId(), gameRoom.getJoinCode(), gameRoom.getState());
    }
}
