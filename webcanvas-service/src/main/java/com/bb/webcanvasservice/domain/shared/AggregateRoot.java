package com.bb.webcanvasservice.domain.shared;

import com.bb.webcanvasservice.domain.shared.event.ApplicationEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 애그리거트 루트가 상속받을 추상 클래스
 */
public abstract class AggregateRoot {
    protected final List<ApplicationEvent> eventQueue = new ArrayList<>();

    /**
     * 애그리거트 루트 이벤트 큐에 들어있는 이벤트를 순차적으로 모두 처리하고 클리어한다.
     *
     * @param eventsPublisher
     */
    public void processEventQueue(Consumer<ApplicationEvent> eventsPublisher) {
        eventQueue.forEach(eventsPublisher::accept);
        eventQueue.clear();
    }
}
