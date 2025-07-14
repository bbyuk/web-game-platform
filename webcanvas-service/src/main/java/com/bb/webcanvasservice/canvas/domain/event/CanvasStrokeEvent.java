package com.bb.webcanvasservice.canvas.domain.event;

import com.bb.webcanvasservice.canvas.domain.model.Stroke;
import com.bb.webcanvasservice.domain.shared.event.ApplicationEvent;

/**
 * 캔버스 stroke 이벤트
 */
public class CanvasStrokeEvent extends ApplicationEvent {

    private final Stroke stroke;

    public CanvasStrokeEvent(Stroke stroke) {
        super("SESSION/CANVAS/STROKE");
        this.stroke = stroke;
    }

    public Stroke getStroke() {
        return stroke;
    }
}
