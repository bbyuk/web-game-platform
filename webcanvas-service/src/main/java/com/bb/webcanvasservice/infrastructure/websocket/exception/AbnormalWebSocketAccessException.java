package com.bb.webcanvasservice.infrastructure.websocket.exception;

import com.bb.webcanvasservice.common.exception.AbnormalAccessException;
import lombok.Getter;

@Getter
public class AbnormalWebSocketAccessException extends AbnormalAccessException {
    private final String ipAddress;

    public AbnormalWebSocketAccessException(String ipAddress) {
        super();
        this.ipAddress = ipAddress;
    }
}
