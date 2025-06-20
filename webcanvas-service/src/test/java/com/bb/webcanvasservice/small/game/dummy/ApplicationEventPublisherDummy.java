package com.bb.webcanvasservice.small.game.dummy;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

public class ApplicationEventPublisherDummy implements ApplicationEventPublisher {
    @Override
    public void publishEvent(ApplicationEvent event) {
        ApplicationEventPublisher.super.publishEvent(event);
    }

    @Override
    public void publishEvent(Object event) {

    }
}
