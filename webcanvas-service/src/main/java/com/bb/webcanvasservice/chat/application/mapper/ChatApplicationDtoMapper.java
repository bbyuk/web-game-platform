package com.bb.webcanvasservice.chat.application.mapper;

import com.bb.webcanvasservice.chat.application.dto.SentChatMessageDto;
import com.bb.webcanvasservice.chat.domain.model.Message;

/**
 * Domain Layer -> Application Layer chat dto mapper
 */
public class ChatApplicationDtoMapper {

    public static SentChatMessageDto toDto(Message message) {
        return new SentChatMessageDto(message.getValue(), message.getSenderId(), message.getTimestamp());
    }
}
