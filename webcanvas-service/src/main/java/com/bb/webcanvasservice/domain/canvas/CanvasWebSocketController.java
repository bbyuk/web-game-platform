package com.bb.webcanvasservice.domain.canvas;

import com.bb.webcanvasservice.domain.canvas.dto.Stroke;
import com.bb.webcanvasservice.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class CanvasWebSocketController {

    private final CanvasService canvasService;

    @MessageMapping("draw/stroke")
    public void broadcastStrokeOnRoom(@RequestBody Stroke stroke, Principal principal) {
        canvasService.broadcastStrokeOnRoom(stroke, SecurityUtils.getUserIdFromPrincipal(principal));
    }

}
