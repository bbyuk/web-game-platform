package com.bb.webcanvasservice.chat.presentation.mapper;

import com.bb.webcanvasservice.chat.application.command.SendMessageCommand;
import com.bb.webcanvasservice.chat.presentation.request.SendMessageRequest;

/**
 * request -> command mapper
 * chat send
 */
public class ChatCommandMapper {
    public static SendMessageCommand toCommand(Long targetId, Long userId, SendMessageRequest request) {
        return new SendMessageCommand(targetId, userId, request.value());
    }
}
