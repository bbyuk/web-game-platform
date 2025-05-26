package com.bb.webcanvasservice.domain.canvas;

import com.bb.webcanvasservice.common.exception.AbnormalAccessException;
import com.bb.webcanvasservice.websocket.properties.WebSocketProperties;
import com.bb.webcanvasservice.domain.canvas.dto.Stroke;
import com.bb.webcanvasservice.domain.game.service.GameRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 캔버스에 그려진 Stroke 획을 받아 브로드캐스팅 처리를 담당하는 서비스 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CanvasService {

    private final SimpMessagingTemplate messagingTemplate;
    private final GameRoomService gameRoomService;
    private final WebSocketProperties webSocketProperties;

    /**
     * 웹 소켓 컨트롤러를 통해 들어온 Stroke 이벤트를 같은 방에 있는 유저들에게 브로드캐스팅한다.
     *
     * @param gameRoomId
     * @param userId
     * @param stroke
     */
    @Transactional(readOnly = true)
    public void broadcastStrokeOnRoom(Long gameRoomId, Long userId, Stroke stroke) {
        log.debug("message sender ====== {}", userId);

        /**
         * validation
         */
        if (!gameRoomService.isEnteredRoom(gameRoomId, userId)) {
            log.error("비정상적인 접근 감지 ::: userId = {} => gameRoomId ={}}", userId, gameRoomId);
            throw new AbnormalAccessException();
        }

        /**
         * gameRoom id에 해당하는 토픽으로 브로드캐스팅
         * /session/{gameRoomId}/canvas 브로커를 구독중인 클라이언트로 stroke 이벤트 브로드캐스팅
         */
        String targetBroker = String.format("%s/%d/%s",
                webSocketProperties.topic().main().gameRoom(),
                gameRoomId,
                webSocketProperties.topic().sub().canvas());
        log.info("send to broker => {}", targetBroker);
        messagingTemplate.convertAndSend(targetBroker, stroke);
    }

}
