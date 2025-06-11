package com.bb.webcanvasservice.presentation.chat.mapper;

import com.bb.webcanvasservice.application.chat.command.SendMessageCommand;
import com.bb.webcanvasservice.presentation.chat.request.SendMessageRequest;

import java.time.LocalDateTime;

/**
 * request -> command mapper
 * chat send
 */
public class SendMessageRequestMapper {
    public static SendMessageCommand toCommand(Long gameRoomId, Long userId, SendMessageRequest request) {
        return new SendMessageCommand(gameRoomId, userId, request.value(), LocalDateTime.now());
    }
}
