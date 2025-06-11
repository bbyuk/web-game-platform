package com.bb.webcanvasservice.presentation.game;

import com.bb.webcanvasservice.common.security.Authenticated;
import com.bb.webcanvasservice.common.security.WebCanvasAuthentication;
import com.bb.webcanvasservice.domain.canvas.dto.Stroke;
import com.bb.webcanvasservice.domain.game.service.GameService;
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
