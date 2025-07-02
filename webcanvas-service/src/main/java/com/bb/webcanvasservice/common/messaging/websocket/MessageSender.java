package com.bb.webcanvasservice.common.messaging.websocket;

public interface MessageSender {
    void send(String destination, Object payload);
}
