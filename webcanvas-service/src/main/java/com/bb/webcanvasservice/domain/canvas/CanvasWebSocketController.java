package com.bb.webcanvasservice.domain.canvas;

import com.bb.webcanvasservice.domain.canvas.dto.Stroke;
import com.bb.webcanvasservice.security.auth.Authenticated;
import com.bb.webcanvasservice.security.auth.WebCanvasAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Canvas 기능 중 브로드캐스팅 관련 API를 담당하는 웹 소켓 컨트롤러
 */
@Controller
@RequiredArgsConstructor
public class CanvasWebSocketController {

    private final CanvasService canvasService;

    /**
     * 요청 받은 Stroke 이벤트를 요청자가 입장해있는 방에 브로드캐스팅한다.
     * @param stroke
     * @param authentication
     */
    @MessageMapping("draw/stroke")
    public void broadcastStrokeOnRoom(@RequestBody Stroke stroke, @Authenticated WebCanvasAuthentication authentication) {
        canvasService.broadcastStrokeOnRoom(stroke, authentication.getUserId());
    }

}
