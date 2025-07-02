package com.bb.webcanvasservice.chat.application.service;

import com.bb.webcanvasservice.chat.application.command.SendMessageCommand;
import com.bb.webcanvasservice.chat.domain.model.Message;
import com.bb.webcanvasservice.common.messaging.websocket.MessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageSender messageSender;

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
     * 동일한 gameRoomId 대상 메세지 브로커를 구독중인 클라이언트로 채팅 메세지를 전송하고 서버에 임시 저장한다.
     * @param command 메세지 전송 커맨드
     */
    @Transactional
    public void sendChatMessage(SendMessageCommand command) {
        String destination = getDestination(command.gameRoomId());

        Message newMessage = Message.create(command.message(), command.senderId(), destination);

        /**
         * TODO 채팅방에 메세지 임시저장
         */

        // 도메인 이벤트 발행
        messageSender.send(destination, newMessage);
    }
}
