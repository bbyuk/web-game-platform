package com.bb.webcanvasservice.chat.domain.event;

import com.bb.webcanvasservice.common.event.ApplicationEvent;
import com.bb.webcanvasservice.chat.domain.model.Message;

/**
 * 메세지 전송시 발행할 이벤트
 */
public class MessageSentEvent extends ApplicationEvent {

    private final Message message;

    public MessageSentEvent(Message message) {
        super("CHAT/MESSAGE_SENT");
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
}
