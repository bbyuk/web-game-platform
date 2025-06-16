package com.bb.webcanvasservice.presentation.canvas.mapper;

import com.bb.webcanvasservice.application.canvas.command.StrokeCommand;
import com.bb.webcanvasservice.domain.canvas.model.Stroke;
import com.bb.webcanvasservice.presentation.canvas.request.StrokeRequest;

/**
 * Presentation layer -> Application Layer command mapper
 * Canvas
 */
public class CanvasCommandMapper {
    public static StrokeCommand toCommand(Long gameSessionId, Long userId, StrokeRequest request) {
        return new StrokeCommand(
                userId,
                gameSessionId,
                request.color(),
                request.lineWidth(),
                request.points()
        );
    }
}
