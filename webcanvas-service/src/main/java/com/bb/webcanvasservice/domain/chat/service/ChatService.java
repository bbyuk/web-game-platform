package com.bb.webcanvasservice.domain.chat.service;

import com.bb.webcanvasservice.domain.chat.event.MessageSentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 채팅 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {


    private final ApplicationEventPublisher eventPublisher;

    /**
     * destination을 만들어 가져온다.
     * @param gameRoomId
     * @return
     */
    private String getDestination(Long gameRoomId) {
        return "/room/" + gameRoomId + "/chat";
    }

    /**
     * 동일한 gameRoomId 대상 메세지 브로커를 구독중인 클라이언트로 채팅 메세지를 전송한다.
     * @param gameRoomId 브로드캐스팅 대상 게임 방 ID
     * @param senderId 전송자 ID
     * @param message 메세지 내용
     * @param timestamp 메세지 전송 시간 timestamp
     */
    public void sendChatMessage(Long gameRoomId, Long senderId, String message, LocalDateTime timestamp) {

        /**
         * TODO 채팅방에 메세지 임시저장
         */

        // 도메인 이벤트 발행
        eventPublisher.publishEvent(new MessageSentEvent(getDestination(gameRoomId), senderId, message, timestamp));
    }


}
