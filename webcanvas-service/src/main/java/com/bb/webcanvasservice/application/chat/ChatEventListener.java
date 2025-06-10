package com.bb.webcanvasservice.application.chat;

import com.bb.webcanvasservice.application.chat.dto.SentChatMessageDto;
import com.bb.webcanvasservice.domain.chat.event.MessageSentEvent;
import com.bb.webcanvasservice.infrastructure.message.MessageSender;
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

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMessageSentEvent(MessageSentEvent event) {
        messageSender.send(event.getDestination(), new SentChatMessageDto(event.getMessage(), event.getSenderId(), event.getTimestamp()));
    }

}
