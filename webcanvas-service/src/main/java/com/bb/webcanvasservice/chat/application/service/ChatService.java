package com.bb.webcanvasservice.chat.application.service;

import com.bb.webcanvasservice.chat.application.command.SendMessageCommand;
import com.bb.webcanvasservice.chat.domain.model.Message;
import com.bb.webcanvasservice.chat.domain.port.game.ChatGamePort;
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

    private final ChatGamePort chatGamePort;

    /**
     * 동일한 gameRoomId 대상 메세지 브로커를 구독중인 클라이언트로 채팅 메세지를 전송하고 서버에 임시 저장한다.
     *
     * @param command 메세지 전송 커맨드
     */
    @Transactional
    public void sendChatMessageToWaitingRoom(SendMessageCommand command) {
        String destination = "/room/" + command.targetId() + "/chat";
        Message newMessage = Message.create(command.message(), command.senderId(), destination);

        /**
         * TODO 채팅방에 메세지 임시저장
         */

        messageSender.send(destination, newMessage);
    }

    @Transactional
    public void sendChatMessageToGameSession(SendMessageCommand command) {
        String destination = "/session/" + command.targetId() + "/chat";
        Message newMessage = Message.create(command.message(), command.senderId(), destination);

        /**
         * TODO 채팅방에 메세지 임시저장
         */
        if (!chatGamePort.isDrawer(command.targetId(), command.senderId())) {
            // drawer가 아닌 경우에만 채팅이 가능하다.

            messageSender.send(destination, newMessage);
            chatGamePort.checkAnswer(command.targetId(), command.senderId(), command.message());
        }
    }
}
