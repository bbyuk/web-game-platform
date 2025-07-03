package com.bb.webcanvasservice.game.domain.adapter;

import com.bb.webcanvasservice.chat.domain.port.game.ChatGameCommandPort;
import com.bb.webcanvasservice.game.application.repository.GameRoomRepository;
import com.bb.webcanvasservice.game.domain.exception.GameRoomNotFoundException;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoom;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameSession;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameTurn;

/**
 * chat -> game command port adapter 구현체
 */
public class ChatGameCommandAdapter implements ChatGameCommandPort {

    private final GameRoomRepository gameRoomRepository;

    public ChatGameCommandAdapter(GameRoomRepository gameRoomRepository) {
        this.gameRoomRepository = gameRoomRepository;
    }

    @Override
    public void checkAnswer(Long gameRoomId, Long senderId, String value) {
        GameRoom gameRoom = gameRoomRepository.findGameRoomById(gameRoomId)
                .orElseThrow(GameRoomNotFoundException::new);

        GameSession gameSession = gameRoom.getCurrentGameSession();
        GameTurn currentTurn = gameSession.getCurrentTurn();

        currentTurn.checkAnswer(senderId, value);

        gameRoomRepository.save(gameRoom);
    }
}
