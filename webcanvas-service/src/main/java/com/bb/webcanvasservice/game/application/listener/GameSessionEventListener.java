package com.bb.webcanvasservice.game.application.listener;

import com.bb.webcanvasservice.game.application.service.GameService;
import com.bb.webcanvasservice.game.domain.event.AllUserInGameSessionLoadedEvent;
import com.bb.webcanvasservice.game.domain.event.GameSessionEndEvent;
import com.bb.webcanvasservice.game.domain.event.GameTurnProgressedEvent;
import com.bb.webcanvasservice.game.domain.exception.GameSessionNotFoundException;
import com.bb.webcanvasservice.game.domain.model.GameSession;
import com.bb.webcanvasservice.game.application.repository.GameSessionRepository;
import com.bb.webcanvasservice.game.application.service.GameTurnTimerService;
import com.bb.webcanvasservice.common.message.MessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * common component
     */
    private final MessageSender messageSender;

    /**
     * 도메인 서비스
     */
    private final GameService gameService;
    private final GameTurnTimerService gameTurnTimerService;

    /**
     * 도메인 레포지토리
     */
    private final GameSessionRepository gameSessionRepository;

    /**
     * 게임 세션이 시작되고 모든 유저가 게임 세션 브로커 토픽을 구독완료하고 로딩 되었을 때 발행되는 이벤트를 핸들링한다.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAllUserInGameSessionLoaded(AllUserInGameSessionLoadedEvent event) {
        messageSender.send("/session/" + event.getGameSessionId(), event);

        GameSession gameSession = gameSessionRepository.findById(event.getGameSessionId())
                .orElseThrow(GameSessionNotFoundException::new);

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
        messageSender.send("/session/" + event.getGameSessionId(), event);
    }

    /**
     * 게임 세션 종료 이벤트 핸들러
     * @param event
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleGameSessionEnd(GameSessionEndEvent event) {
        gameTurnTimerService.stopTurnTimer(event.getGameRoomId());
        messageSender.send("/session/" + event.getGameSessionId(), event);

        /**
         * TODO - 게임 결과 리턴할 수 있도록 변경
         */
    }


}
