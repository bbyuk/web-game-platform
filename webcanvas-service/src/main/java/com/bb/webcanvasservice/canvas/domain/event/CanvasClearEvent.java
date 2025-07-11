package com.bb.webcanvasservice.canvas.domain.event;

import com.bb.webcanvasservice.domain.shared.event.ApplicationEvent;

/**
 * 캔버스 초기화 이벤트
 */
public class CanvasClearEvent extends ApplicationEvent {
    public CanvasClearEvent() {
        super("SESSION/CANVAS/CLEAR");
    }
}
