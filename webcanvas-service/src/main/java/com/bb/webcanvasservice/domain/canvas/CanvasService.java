package com.bb.webcanvasservice.domain.canvas;

import com.bb.webcanvasservice.domain.canvas.dto.Stroke;
import com.bb.webcanvasservice.domain.canvas.dto.StrokeMessage;
import com.bb.webcanvasservice.domain.game.GameRoom;
import com.bb.webcanvasservice.domain.game.GameRoomService;
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
    public static final String CANVAS_DESTINATION_PREFIX = "/canvas/";

    /**
     * 웹 소켓 컨트롤러를 통해 들어온 Stroke 이벤트를 같은 방에 있는 유저들에게 브로드캐스팅한다.
     * @param stroke
     *
     */
    @Transactional(readOnly = true)
    public void broadcastStrokeOnRoom(Stroke stroke, Long userId) {
        log.debug("message sender ====== {}", userId);

        /**
         * 현재 입장한 방 조회
         */
        GameRoom enteredGameRoom = gameRoomService.findEnteredGameRoom(userId);

        /**
         * gameRoom id에 해당하는 토픽으로 브로드캐스팅
         * /canvas/{gameRoomId} 브로커를 구독중인 클라이언트로 stroke 이벤트 브로드캐스팅
         */
        messagingTemplate.convertAndSend(String.format("%s%d", CANVAS_DESTINATION_PREFIX ,enteredGameRoom.getId()), stroke);
    }

}
