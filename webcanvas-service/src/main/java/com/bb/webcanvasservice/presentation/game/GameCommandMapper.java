package com.bb.webcanvasservice.presentation.game;

import com.bb.webcanvasservice.application.game.command.EnterGameRoomCommand;
import com.bb.webcanvasservice.application.game.command.StartGameCommand;
import com.bb.webcanvasservice.domain.game.model.GameRoomEntranceRole;

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
