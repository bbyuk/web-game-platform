package com.bb.webcanvasservice.domain.game.listener;

import com.bb.webcanvasservice.domain.game.event.GameRoomEntranceEvent;
import com.bb.webcanvasservice.domain.game.event.GameRoomExitEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 게임 관련 이벤트 리스너
 */
@Component
@RequiredArgsConstructor
public class GameEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 유저가 게임방에 입장할 때 게임 방 메세지 브로커로 입장 이벤트를 push한다.
     * @param event
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleGameRoomEntrance(GameRoomEntranceEvent event) {
        messagingTemplate.convertAndSend("/session/" + event.getGameRoomId(), event);
    }

    /**
     * 유저가 게임방에서 퇴장할 떄 게임 방 메세지 브로커로 퇴장 이벤트를 push한다.
     * @param event
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleGameRoomExit(GameRoomExitEvent event) {
        messagingTemplate.convertAndSend("/session/" + event.getGameRoomId(), event);
    }

}
