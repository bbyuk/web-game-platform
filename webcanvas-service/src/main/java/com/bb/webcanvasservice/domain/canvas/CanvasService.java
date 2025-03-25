package com.bb.webcanvasservice.domain.canvas;

import com.bb.webcanvasservice.domain.canvas.dto.Stroke;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CanvasService {

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastStrokeOnRoom(Stroke stroke) {
        log.debug("stroke occured at gameRoom = {}", stroke.gameRoomId());
        log.debug("stroke made by user = {}", stroke.userId());

        /**
         * 1. gameRoom에 속해있는 유저들 목록 조회
         * 2. gameRoom에 속해있는 유저들 타겟 토픽에 stroke 메세지 전송
         */
        messagingTemplate.convertAndSend("/topic/canvas", stroke);
    }
}
