package com.bb.webcanvasservice.game.domain.adapter;

import com.bb.webcanvasservice.chat.domain.port.game.ChatGameCommandPort;
import com.bb.webcanvasservice.game.domain.repository.GameRoomRepository;
import com.bb.webcanvasservice.game.domain.exception.GameRoomNotFoundException;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoom;
import org.springframework.context.ApplicationEventPublisher;

/**
 * chat -> game command port adapter 구현체
 */
public class ChatGameCommandAdapter implements ChatGameCommandPort {

    private final GameRoomRepository gameRoomRepository;

    private final ApplicationEventPublisher eventPublisher;

    public ChatGameCommandAdapter(GameRoomRepository gameRoomRepository, ApplicationEventPublisher eventPublisher) {
        this.gameRoomRepository = gameRoomRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void checkAnswer(Long gameRoomId, Long senderId, String value) {
        GameRoom gameRoom = gameRoomRepository.findGameRoomById(gameRoomId)
                .orElseThrow(GameRoomNotFoundException::new);

        gameRoom.checkAnswer(senderId, value);

        gameRoom.processEventQueue(eventPublisher::publishEvent);
        gameRoomRepository.save(gameRoom);
    }
}
