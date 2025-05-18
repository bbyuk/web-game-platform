package com.bb.webcanvasservice.websocket.security;

public interface AbnormalAccessRegistry {
    void add(String ipAddress);
}
