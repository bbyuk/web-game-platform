package com.bb.webcanvasservice.domain.canvas;

import com.bb.webcanvasservice.domain.canvas.dto.Stroke;
import com.bb.webcanvasservice.security.auth.Authenticated;
import com.bb.webcanvasservice.security.auth.WebCanvasAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

/**
 * Canvas 기능 중 브로드캐스팅 관련 API를 담당하는 웹 소켓 컨트롤러
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class CanvasWebSocketController {

    private final CanvasService canvasService;

    /**
     * 요청 받은 Stroke 이벤트를 요청자가 입장해있는 방에 브로드캐스팅한다.
     * @param stroke
     * @param authentication
     */
    @MessageMapping("canvas/{gameRoomId}/draw/stroke")
    public void broadcastStrokeOnRoom(@DestinationVariable("gameRoomId") Long gameRoomId, Stroke stroke, @Authenticated WebCanvasAuthentication authentication) {
        log.info("클라이언트로부터 메세지 받음");
        canvasService.broadcastStrokeOnRoom(stroke, authentication.getUserId());
    }

    /**
     * 게임 방 입장 후 웹소켓 브로커 구독에 대한 validation 처리를 담당
     * @param gameRoomId
     */
    @SubscribeMapping("canvas/{gameRoomId}")
    public void handleSubscribeGameRoom(@DestinationVariable("gameRoomId") Long gameRoomId) {
        log.info("게임 방 {}에 대한 구독 요청");
    }
}
