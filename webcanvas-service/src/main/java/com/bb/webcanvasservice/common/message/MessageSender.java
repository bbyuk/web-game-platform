package com.bb.webcanvasservice.common.message;

public interface MessageSender {
    void send(String destination, Object payload);
}
