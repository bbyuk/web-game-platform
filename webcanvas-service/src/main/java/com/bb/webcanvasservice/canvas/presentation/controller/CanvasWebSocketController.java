package com.bb.webcanvasservice.canvas.presentation.controller;

import com.bb.webcanvasservice.canvas.application.service.CanvasService;
import com.bb.webcanvasservice.canvas.presentation.request.StrokeRequest;
import com.bb.webcanvasservice.infrastructure.security.http.Authenticated;
import com.bb.webcanvasservice.infrastructure.security.http.WebCanvasAuthentication;
import com.bb.webcanvasservice.canvas.presentation.mapper.CanvasCommandMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
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
     * @param request   stroke 요청
     * @param authentication 인증 객체
     */
    @MessageMapping("session/{gameSessionId}/canvas/stroke")
    public void broadcastStrokeOnRoom(@DestinationVariable("gameSessionId") Long gameSessionId, StrokeRequest request, @Authenticated WebCanvasAuthentication authentication) {
        log.info("클라이언트로부터 메세지 받음");

        canvasService.broadcastStrokeOnRoom(CanvasCommandMapper.toCommand(authentication.getUserId(), gameSessionId, request));
    }
}
