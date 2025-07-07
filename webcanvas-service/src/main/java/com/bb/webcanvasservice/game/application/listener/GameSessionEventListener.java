package com.bb.webcanvasservice.game.application.listener;

import com.bb.webcanvasservice.common.messaging.websocket.MessageSender;
import com.bb.webcanvasservice.game.application.command.ProcessToNextTurnCommand;
import com.bb.webcanvasservice.game.application.service.GameService;
import com.bb.webcanvasservice.game.domain.event.*;
import com.bb.webcanvasservice.game.domain.port.external.GameTurnTimerPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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

    /**
     * 도메인 port
     */
    private final GameTurnTimerPort gameTurnTimerPort;


    /**
     * 게임 세션이 시작되고 모든 유저가 게임 세션 브로커 토픽을 구독완료하고 로딩 되었을 때 발행되는 이벤트를 핸들링한다.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAllUserInGameSessionLoaded(AllUserInGameSessionLoadedEvent event) {
        log.debug("모든 유저가 로딩되어 게임을 시작 gameSessionId = {}", event.getGameSessionId());
        messageSender.send("/session/" + event.getGameSessionId(), event);
        gameService.processToNextTurn(new ProcessToNextTurnCommand(event.getGameRoomId(), event.getGameSessionId(), event.getTimePerTurn(), false));
    }

    /**
     * 게임 턴 타이머 리셋 요청 이벤트 핸들링
     *
     * @param event
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleGameTurnTimerResetRequested(GameTurnTimerResetRequestedEvent event) {
        log.debug("리셋요청 = {}", event);
        gameTurnTimerPort.registerTurnTimer(
                new ProcessToNextTurnCommand(event.getGameRoomId(),
                        event.getGameSessionId(),
                        event.getPeriod(),
                        event.isAnswered()
                ), event.getPeriod()
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleGameTurnTimerRegisterRequested(GameTurnTimerRegisterRequestedEvent event) {
        log.debug("등록 요청 = {}", event);
        gameTurnTimerPort.registerTurnTimer(
                new ProcessToNextTurnCommand(event.getGameRoomId(),
                        event.getGameSessionId(),
                        event.getPeriod(),
                        event.isAnswered()
                ), 0
        );
    }

    /**
     * 게임 턴 진행 이후 이벤트 핸들러
     * game session 메세지 브로커에 턴 진행 이벤트 메세지 push
     *
     * @param event
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleGameTurnProgressed(GameTurnProgressedEvent event) {
        messageSender.send("/session/" + event.getGameSessionId(), event);
    }

    /**
     * 게임 세션 종료 이벤트 핸들러
     *
     * @param event
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleGameSessionEnd(GameSessionEndEvent event) {
        gameTurnTimerPort.stopTurnTimer(event.getGameSessionId());
        messageSender.send("/session/" + event.getGameSessionId(), event);

        /**
         * TODO - 게임 결과 리턴할 수 있도록 변경
         */
    }

    /**
     * 게임 턴 진행 요청 이벤트 핸들러 (게임 턴 진행 이전)
     *
     * @param event
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleGameTurnProgressRequested(GameTurnProgressRequestedEvent event) {
        log.debug("턴 진행 요청 이벤트 발생 ====== {}", event);

        gameService.processToNextTurn(new ProcessToNextTurnCommand(event.getGameRoomId(), event.getGameSessionId(), event.getGameTurnPeriod(), event.getAnswererId() != null));
    }
}
