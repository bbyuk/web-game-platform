package com.bb.webcanvasservice.domain.game.listener;

import com.bb.webcanvasservice.domain.game.entity.GameSession;
import com.bb.webcanvasservice.domain.game.event.AllUserInGameSessionLoadedEvent;
import com.bb.webcanvasservice.domain.game.event.GameSessionEndEvent;
import com.bb.webcanvasservice.domain.game.event.GameTurnProgressedEvent;
import com.bb.webcanvasservice.domain.game.service.GameService;
import com.bb.webcanvasservice.domain.game.service.GameTurnTimerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 게임 세션 관련 이벤트 리스너
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GameSessionEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    private final GameService gameService;
    private final GameTurnTimerService gameTurnTimerService;


    /**
     * 게임 세션이 시작되고 모든 유저가 게임 세션 브로커 토픽을 구독완료하고 로딩 되었을 때 발행되는 이벤트를 핸들링한다.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAllUserInGameSessionLoaded(AllUserInGameSessionLoadedEvent event) {
        messagingTemplate.convertAndSend("/session/" + event.getGameSessionId(), event);

        GameSession gameSession = gameService.findGameSession(event.getGameSessionId());
        gameTurnTimerService.registerTurnTimer(
                event.getGameRoomId(),
                event.getGameSessionId(),
                gameSession.getTimePerTurn(),
                gameService::processToNextTurn
        );
    }

    /**
     * 게임 턴 진행 이벤트 핸들러
     * gameRoom 메세지 브로커에 턴 진행 이벤트 메세지 push
     *
     * @param event
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleGameTurnProgressed(GameTurnProgressedEvent event) {
        messagingTemplate.convertAndSend("/session/" + event.getGameSessionId(), event);
    }

    /**
     * 게임 세션 종료 이벤트 핸들러
     * @param event
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleGameSessionEnd(GameSessionEndEvent event) {
        gameTurnTimerService.stopTurnTimer(event.getGameRoomId());
        messagingTemplate.convertAndSend("/session/" + event.getGameSessionId(), event);

        /**
         * TODO - 게임 결과 리턴할 수 있도록 변경
         */
    }


}
