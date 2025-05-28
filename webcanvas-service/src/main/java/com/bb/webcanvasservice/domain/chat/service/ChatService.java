package com.bb.webcanvasservice.domain.chat.service;

import com.bb.webcanvasservice.domain.chat.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * 채팅 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {


    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * 동일한 gameRoomId 대상 메세지 브로커를 구독중인 클라이언트로 채팅 메세지를 전송한다.
     * @param gameRoomId
     * @param message
     * @param userId
     */
    public void sendChatMessage(Long gameRoomId, ChatMessage message, Long userId) {
        if (!message.getSenderId().equals(userId)) {
            log.debug("메세지 전송자와 요청자 ID가 서로 다릅니다.");
            log.debug("message sender id : {}", message.getSenderId());
            log.debug("requester id : {}", userId);
        }
        simpMessagingTemplate.convertAndSend("/session/" + gameRoomId + "/chat", message);
    }
}
