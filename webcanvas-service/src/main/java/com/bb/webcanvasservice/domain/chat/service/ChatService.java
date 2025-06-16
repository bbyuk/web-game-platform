package com.bb.webcanvasservice.domain.chat.service;

import com.bb.webcanvasservice.common.message.MessageSender;
import com.bb.webcanvasservice.domain.chat.event.MessageSentEvent;
import com.bb.webcanvasservice.domain.chat.model.Message;

/**
 * 채팅 서비스
 */
public class ChatService {

    private final MessageSender messageSender;

    public ChatService(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    /**
     * destination을 만들어 가져온다.
     *
     * @param gameRoomId
     * @return
     */
    private String getDestination(Long gameRoomId) {
        return "/room/" + gameRoomId + "/chat";
    }

    /**
     * 동일한 gameRoomId 대상 메세지 브로커를 구독중인 클라이언트로 채팅 메세지를 전송한다.
     *
     * @param gameRoomId 브로드캐스팅 대상 게임 방 ID
     * @param senderId   전송자 ID
     * @param value      메세지 내용
     */
    public void sendChatMessage(Long gameRoomId, Long senderId, String value) {
        String destination = getDestination(gameRoomId);
        Message newMessage = Message.createNewMessage(value, senderId, destination);

        /**
         * TODO 채팅방에 메세지 임시저장
         */

        // 도메인 이벤트 발행
        messageSender.send(destination, new MessageSentEvent(newMessage));
    }


}
