package com.bb.webcanvasservice.chat.application.listener;

import com.bb.webcanvasservice.chat.application.mapper.ChatApplicationDtoMapper;
import com.bb.webcanvasservice.common.messaging.websocket.MessageSender;
import com.bb.webcanvasservice.chat.domain.event.MessageSentEvent;
import com.bb.webcanvasservice.chat.domain.model.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * chat application layer 도메인 이벤트리스너
 */
@Component
@RequiredArgsConstructor
public class ChatEventListener {

    private final MessageSender messageSender;

    /**
     * TODO 성능 체크 후 phase 조절 필요
     * @param event
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMessageSentEvent(MessageSentEvent event) {
        Message message = event.getMessage();
        messageSender.send(message.getDestination(), ChatApplicationDtoMapper.toDto(message));
    }

}
