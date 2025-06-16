package com.bb.webcanvasservice.application.chat.service;

import com.bb.webcanvasservice.application.chat.command.SendMessageCommand;
import com.bb.webcanvasservice.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatApplicationService {

    private final ChatService chatService;

    /**
     * 동일한 gameRoomId 대상 메세지 브로커를 구독중인 클라이언트로 채팅 메세지를 전송하고 서버에 임시 저장한다.
     * @param command 메세지 전송 커맨드
     */
    @Transactional
    public void sendChatMessage(SendMessageCommand command) {
        chatService.sendChatMessage(command.gameRoomId(), command.senderId(), command.message());
    }
}
