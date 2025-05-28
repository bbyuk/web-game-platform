package com.bb.webcanvasservice.domain.game.listener;

import com.bb.webcanvasservice.domain.game.entity.GameSession;
import com.bb.webcanvasservice.domain.game.event.GameSessionEndEvent;
import com.bb.webcanvasservice.domain.game.event.GameSessionStartEvent;
import com.bb.webcanvasservice.domain.game.service.GameService;
import com.bb.webcanvasservice.domain.game.service.GameTurnTimerService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 게임 세션 관련 이벤트 리스너
 */
@Component
@RequiredArgsConstructor
public class GameSessionEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    private final GameService gameService;
    private final GameTurnTimerService gameTurnTimerService;

    /**
     * 게임 세션 시작시 발생하는 이벤트 핸들링
     * <p>
     * 클라이언트로 게임 시작 event 메세지 push
     * 게임 턴 타이머 등록
     *
     * @param event
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleGameSessionStart(GameSessionStartEvent event) {
        messagingTemplate.convertAndSend("/session/" + event.getGameRoomId(), event);

        GameSession gameSession = gameService.findGameSession(event.getGameSessionId());

        gameTurnTimerService.registerTurnTimer(
                event.getGameRoomId(),
                gameSession.getTimePerTurn(),
                gameService::processToNextTurn,
                gameService::isGameEnd,
                gameService::endGame
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleGameSessionEnd(GameSessionEndEvent event) {
        messagingTemplate.convertAndSend("/session/" + event.getGameRoomId(), event);

        /**
         * TODO - 게임 결과 리턴할 수 있도록 변경
         */
    }


}
