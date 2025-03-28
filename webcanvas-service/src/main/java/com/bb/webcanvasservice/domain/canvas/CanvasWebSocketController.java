package com.bb.webcanvasservice.domain.canvas;

import com.bb.webcanvasservice.domain.canvas.dto.Stroke;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class CanvasWebSocketController {

    private final CanvasService canvasService;

    @MessageMapping("draw/stroke")
    public void broadcastStrokeOnRoom(Stroke stroke) {
        canvasService.broadcastStrokeOnRoom(stroke);
    }

}
