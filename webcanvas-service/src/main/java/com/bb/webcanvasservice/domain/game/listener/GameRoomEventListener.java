package com.bb.webcanvasservice.domain.game.listener;

import com.bb.webcanvasservice.domain.game.entity.GameSession;
import com.bb.webcanvasservice.domain.game.event.*;
import com.bb.webcanvasservice.domain.game.service.GameService;
import com.bb.webcanvasservice.domain.game.service.GameTurnTimerService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 게임 방 관련 이벤트 리스너
 */
@Component
@RequiredArgsConstructor
public class GameRoomEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    private final GameService gameService;
    private final GameTurnTimerService gameTurnTimerService;
    /**
     * 유저가 게임방에 입장할 때 게임 방 메세지 브로커로 입장 이벤트를 push한다.
     * @param event
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleGameRoomEntrance(GameRoomEntranceEvent event) {
        messagingTemplate.convertAndSend("/room/" + event.getGameRoomId(), event);
    }

    /**
     * 유저가 게임방에서 퇴장할 떄 게임 방 메세지 브로커로 퇴장 이벤트를 push한다.
     * @param event
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleGameRoomExit(GameRoomExitEvent event) {
        messagingTemplate.convertAndSend("/room/" + event.getGameRoomId(), event);
    }

    /**
     * 게임 방 호스트 변경 시 게임 방 메세지 브로커로 호스트 변경 이벤트를 push한다.
     * @param event
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleGameRoomHostChange(GameRoomHostChangedEvent event) {
        messagingTemplate.convertAndSend("/room/" + event.getGameRoomId(), event);
    }

    /**
     * 게임 방에 접속한 유저들의 레디 상태 변경시 게임 방 메세지 브로커로 레디 변경 이벤트를 push한다.
     * @param event
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserReadyChanged(UserReadyChanged event) {
        messagingTemplate.convertAndSend("/room/" + event.getGameRoomId(), event);
    }

    /**
     * 게임 세션 시작시 발생하는 이벤트 핸들러
     * <p>
     * 클라이언트로 게임 시작 event 메세지 push
     * 게임 턴 타이머 등록
     *
     * @param event
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleGameSessionStart(GameSessionStartEvent event) {
        messagingTemplate.convertAndSend("/room/" + event.getGameRoomId(), event);

        GameSession gameSession = gameService.findGameSession(event.getGameSessionId());


        gameTurnTimerService.registerTurnTimer(
                event.getGameRoomId(),
                event.getGameSessionId(),
                gameSession.getTimePerTurn(),
                gameService::processToNextTurn
        );
    }
}
