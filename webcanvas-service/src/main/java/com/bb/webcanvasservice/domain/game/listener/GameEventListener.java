package com.bb.webcanvasservice.domain.game.listener;

import com.bb.webcanvasservice.domain.game.event.GameRoomEntranceEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * 게임 관련 이벤트 리스너
 */
@Component
@RequiredArgsConstructor
public class GameEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleGameRoomEntrance(GameRoomEntranceEvent event) {
        messagingTemplate.convertAndSend("/session/" + event.gameRoomId(), event);
    }

}
