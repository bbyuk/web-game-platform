package com.bb.webcanvasservice.domain.game.listener;

import com.bb.webcanvasservice.domain.game.event.GameRoomEntranceEvent;
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
     * 게임방에 입장한 유저의 정보를 메세지로 리턴한다.
     * @param event
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleGameRoomEntrance(GameRoomEntranceEvent event) {
        messagingTemplate.convertAndSend("/session/" + event.getGameRoomId(), event);
    }

}
