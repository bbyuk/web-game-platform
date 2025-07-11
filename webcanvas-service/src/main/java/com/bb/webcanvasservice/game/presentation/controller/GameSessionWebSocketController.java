package com.bb.webcanvasservice.game.presentation.controller;

import com.bb.webcanvasservice.infrastructure.security.http.Authenticated;
import com.bb.webcanvasservice.infrastructure.security.http.WebCanvasAuthentication;
import com.bb.webcanvasservice.game.application.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GameSessionWebSocketController {

    private final GameService gameService;

    /**
     * 요청 받은 Stroke 이벤트를 요청자가 입장해있는 방에 브로드캐스팅한다.
     * @param authentication
     */
    @SubscribeMapping("session/{gameSessionId}")
    public void broadcastStrokeOnRoom(@DestinationVariable("gameSessionId") Long gameSessionId, @Authenticated WebCanvasAuthentication authentication) {
        gameService.successSubscription(gameSessionId, authentication.getUserId());
    }
}
