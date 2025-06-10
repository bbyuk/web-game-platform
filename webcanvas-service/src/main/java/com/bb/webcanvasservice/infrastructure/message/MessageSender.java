package com.bb.webcanvasservice.infrastructure.message;

public interface MessageSender {
    void send(String destination, Object payload);
}
