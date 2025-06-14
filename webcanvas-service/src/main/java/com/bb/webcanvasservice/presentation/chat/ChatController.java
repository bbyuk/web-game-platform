package com.bb.webcanvasservice.presentation.chat;

import com.bb.webcanvasservice.application.chat.ChatApplicationService;
import com.bb.webcanvasservice.common.security.Authenticated;
import com.bb.webcanvasservice.common.security.WebCanvasAuthentication;
import com.bb.webcanvasservice.presentation.chat.request.SendMessageRequest;
import com.bb.webcanvasservice.presentation.chat.mapper.SendMessageRequestMapper;
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

    private final ChatApplicationService chatApplicationService;

    @MessageMapping("room/{gameRoomId}/chat/send")
    @Operation(summary = "채팅 메세지 전송", description = "채팅 메세지를 같은 메세지 브로커를 구독하는 클라이언트로 전송한다.")
    public void sendChatMessage(@DestinationVariable("gameRoomId") Long gameRoomId,
                                SendMessageRequest request,
                                @Authenticated WebCanvasAuthentication authentication) {
        chatApplicationService.sendChatMessage(SendMessageRequestMapper.toCommand(gameRoomId, authentication.getUserId(), request));
    }

}
