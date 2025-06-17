package com.bb.webcanvasservice.game.presentation.mapper;

import com.bb.webcanvasservice.game.application.command.EnterGameRoomCommand;
import com.bb.webcanvasservice.game.application.command.StartGameCommand;
import com.bb.webcanvasservice.game.domain.model.GameRoomEntranceRole;

/**
 * presentation layer -> application layer command mapper
 */
public class GameCommandMapper {

    public static EnterGameRoomCommand toEnterGameRoomCommand(Long gameRoomId, Long userId) {
        return new EnterGameRoomCommand(gameRoomId, userId, GameRoomEntranceRole.GUEST);
    }

    public static StartGameCommand toStartGameCommand(Long gameRoomId, int turnCount, int timePerTurn, Long userId) {
        return new StartGameCommand(gameRoomId, turnCount, timePerTurn, userId);
    }
}
