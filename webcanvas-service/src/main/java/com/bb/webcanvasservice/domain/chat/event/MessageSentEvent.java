package com.bb.webcanvasservice.domain.chat.event;

import com.bb.webcanvasservice.common.event.ApplicationEvent;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MessageSentEvent extends ApplicationEvent {

    private final String destination;
    private final Long senderId;
    private final String message;
    private final LocalDateTime timestamp;

    public MessageSentEvent(String destination, Long senderId, String message, LocalDateTime timestamp) {
        super("CHAT/MESSAGE_SENT");
        this.destination = destination;
        this.senderId = senderId;
        this.message = message;
        this.timestamp = timestamp;
    }
}
