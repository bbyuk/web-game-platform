package com.bb.webcanvasservice.chat.presentation.controller;

import com.bb.webcanvasservice.chat.application.service.ChatService;
import com.bb.webcanvasservice.chat.presentation.mapper.ChatCommandMapper;
import com.bb.webcanvasservice.chat.presentation.request.SendMessageRequest;
import com.bb.webcanvasservice.infrastructure.security.http.Authenticated;
import com.bb.webcanvasservice.infrastructure.security.http.WebCanvasAuthentication;
import io.swagger.v3.oas.annotations.Operation;
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

    @MessageMapping("room/{gameRoomId}/chat/send")
    @Operation(summary = "게임 대기 방 채팅 메세지 전송", description = "채팅 메세지를 같은 방에 입장한 유저들에게 전송한다.")
    public void sendChatMessageAtWaitingRoom(@DestinationVariable("gameRoomId") Long gameRoomId,
                                SendMessageRequest request,
                                @Authenticated WebCanvasAuthentication authentication) {
        chatService.sendChatMessageToWaitingRoom(ChatCommandMapper.toCommand(gameRoomId, authentication.getUserId(), request));
    }

    @MessageMapping("session/{gameSessionId}/chat/send")
    @Operation(summary = "게임 세션 채팅 메세지 전송", description = "채팅 메세지를 게임 세션 내로 전송하고, 정답을 체크한다.")
    public void sendChatMessageAtGameSession(@DestinationVariable("gameRoomId") Long gameRoomId,
                                SendMessageRequest request,
                                @Authenticated WebCanvasAuthentication authentication) {
        chatService.sendChatMessageToGameSession(ChatCommandMapper.toCommand(gameRoomId, authentication.getUserId(), request));
    }

}
