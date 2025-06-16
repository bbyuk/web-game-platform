package com.bb.webcanvasservice.application.chat;

import com.bb.webcanvasservice.application.chat.dto.SentChatMessageDto;
import com.bb.webcanvasservice.domain.chat.model.Message;

/**
 * Domain Layer -> Application Layer chat dto mapper
 */
public class ChatApplicationDtoMapper {

    public static SentChatMessageDto toDto(Message message) {
        return new SentChatMessageDto(message.getValue(), message.getSenderId(), message.getTimestamp());
    }
}
