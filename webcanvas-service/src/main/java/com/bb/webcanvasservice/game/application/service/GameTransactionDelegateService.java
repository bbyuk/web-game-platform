package com.bb.webcanvasservice.game.application.service;

import com.bb.webcanvasservice.game.domain.event.AllUserInGameSessionLoadedEvent;
import com.bb.webcanvasservice.game.domain.exception.GameSessionNotFoundException;
import com.bb.webcanvasservice.game.domain.model.session.GameSession;
import com.bb.webcanvasservice.game.domain.repository.GameSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게임 서비스 내에서 새 스레드 작업이 필요한 경우 Transaction 처리 작업을
 * 위임 받아 처리할 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GameTransactionDelegateService {

    private final GameSessionRepository gameSessionRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public boolean successToSubscribeTransaction(Long gameSessionId, Long userId) {
        GameSession gameSession = loadPlayerToGameSession(gameSessionId, userId);
        if (gameSession.isPlaying()) {
            return false;
        }

        if (gameSession.isAllPlayersLoaded()) {
            startGameSession(gameSession);
            return true;
        }

        return false;
    }


    /**
     * 모든 유저들이 로드 된 후 게임 세션을 시작한다
     *
     * @param gameSession 게임 세션
     */
    private void startGameSession(GameSession gameSession) {
        log.debug("game session {} all loaded", gameSession.id());

        gameSession.start();

        gameSessionRepository.save(gameSession);

        eventPublisher.publishEvent(new AllUserInGameSessionLoadedEvent(gameSession.id(), gameSession.gameRoomId(), gameSession.timePerTurn(), gameSession.delayBetweenTurns()));
    }

    /**
     * 대상 게임 세션에 대상 유저를 플레이어로 로드한다.
     *
     * @param gameSessionId 대상 게임 세션 ID
     * @param userId        대상 유저 ID
     * @return 저장된 게임 세션
     */
    private GameSession loadPlayerToGameSession(Long gameSessionId, Long userId) {
        GameSession gameSession = gameSessionRepository.findGameSessionById(gameSessionId).orElseThrow(GameSessionNotFoundException::new);
        gameSession.loadPlayer(userId);

        return gameSessionRepository.save(gameSession);
    }
}
