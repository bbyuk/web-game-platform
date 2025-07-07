package com.bb.webcanvasservice.game.domain.adapter;

import com.bb.webcanvasservice.chat.domain.port.game.ChatGamePort;
import com.bb.webcanvasservice.game.domain.exception.GameSessionNotFoundException;
import com.bb.webcanvasservice.game.domain.model.session.GameSession;
import com.bb.webcanvasservice.game.domain.repository.GameSessionRepository;
import org.springframework.context.ApplicationEventPublisher;

/**
 * chat -> game command port adapter 구현체
 */
public class ChatGameAdapter implements ChatGamePort {

    private final GameSessionRepository gameSessionRepository;

    private final ApplicationEventPublisher eventPublisher;

    public ChatGameAdapter(GameSessionRepository gameSessionRepository, ApplicationEventPublisher eventPublisher) {
        this.gameSessionRepository = gameSessionRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void checkAnswer(Long gameSessionId, Long senderId, String value) {
        GameSession gameSession = gameSessionRepository.findGameSessionById(gameSessionId)
                .orElseThrow(GameSessionNotFoundException::new);

        gameSession.checkAnswer(senderId, value);
        gameSession.processEventQueue(eventPublisher::publishEvent);
    }

    @Override
    public boolean isDrawer(Long gameSessionId, Long senderId) {
        GameSession gameSession = gameSessionRepository.findGameSessionById(gameSessionId).orElseThrow(GameSessionNotFoundException::new);
        return gameSession.getCurrentTurn().isDrawer(senderId);
    }
}
