package com.bb.webcanvasservice.domain.shared.event;

import lombok.Getter;

/**
 * 애플리케이션 이벤트 추상 클래스로
 * 모든 애플리케이션 이벤트는 String type의 id를 갖는다.
 */
@Getter
public abstract class ApplicationEvent {
    protected final String event;

    protected ApplicationEvent(String event) {
        this.event = event;
    }
}
