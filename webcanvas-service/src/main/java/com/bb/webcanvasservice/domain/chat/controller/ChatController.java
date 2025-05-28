package com.bb.webcanvasservice.domain.chat.controller;

import com.bb.webcanvasservice.common.security.Authenticated;
import com.bb.webcanvasservice.common.security.WebCanvasAuthentication;
import com.bb.webcanvasservice.domain.chat.dto.ChatMessage;
import com.bb.webcanvasservice.domain.chat.service.ChatService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "Chat API", description = "게임 플레이 관련 API")
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("session/{gameRoomId}/chat/send")
    public void sendChat(@DestinationVariable("gameRoomId") Long gameRoomId, ChatMessage message, @Authenticated WebCanvasAuthentication authentication) {

    }

}
