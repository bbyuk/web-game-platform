package com.bb.webcanvasservice.application.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "application layer -> infrastructure layer 전달 chat message")
public record SentChatMessageDto(

        @Schema(description = "메세지 값")
        String message,

        @Schema(description = "전송자 ID")
        Long senderId,

        @Schema(description = "전송 시간 타임스탬프")
        LocalDateTime timestamp
) {
}
