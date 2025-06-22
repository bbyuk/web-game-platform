package com.bb.webcanvasservice.game.presentation.mapper;

import com.bb.webcanvasservice.game.application.command.JoinGameRoomCommand;
import com.bb.webcanvasservice.game.application.command.ExitGameRoomCommand;
import com.bb.webcanvasservice.game.application.command.StartGameCommand;
import com.bb.webcanvasservice.game.application.command.UpdateReadyCommand;

/**
 * presentation layer -> application layer command mapper
 */
public class GameCommandMapper {

    public static JoinGameRoomCommand toEnterGameRoomCommand(Long gameRoomId, Long userId) {
        return new JoinGameRoomCommand(gameRoomId, userId);
    }

    public static StartGameCommand toStartGameCommand(Long gameRoomId, int turnCount, int timePerTurn, Long userId) {
        return new StartGameCommand(gameRoomId, turnCount, timePerTurn, userId);
    }

    public static ExitGameRoomCommand toExitGameRoomCommand(Long gameRoomParticipantId, Long userId) {
        return new ExitGameRoomCommand(gameRoomParticipantId, userId);
    }

    public static UpdateReadyCommand toUpdateReadyCommand(Long gameRoomParticipantId, Long userId, boolean ready) {
        return new UpdateReadyCommand(gameRoomParticipantId, userId, ready);
    }
}
