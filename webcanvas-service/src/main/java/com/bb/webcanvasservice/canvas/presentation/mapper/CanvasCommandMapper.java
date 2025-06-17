package com.bb.webcanvasservice.canvas.presentation.mapper;

import com.bb.webcanvasservice.canvas.application.command.StrokeCommand;
import com.bb.webcanvasservice.canvas.presentation.request.StrokeRequest;

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
