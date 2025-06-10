package com.bb.webcanvasservice.domain.chat.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Schema(description = "채팅 메세지 모델")
public class Message {

    @Schema(description = "채팅 메세지 값")
    private final String value;

    @Schema(description = "발신자 ID")
    private final Long senderId;

    @Schema(description = "타임스탬프")
    private final LocalDateTime timestamp = LocalDateTime.now();


    public Message(String value, Long senderId) {
        this.value = value;
        this.senderId = senderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(value, message.value) &&
                Objects.equals(senderId, message.senderId) &&
                Objects.equals(timestamp, message.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, senderId, timestamp);
    }
}
