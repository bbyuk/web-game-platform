package com.bb.webcanvasservice.domain.chat.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "채팅 메세지 DTO")
public class ChatMessage {

    @Schema(description = "채팅 메세지 값")
    private String value;

    @Schema(description = "발신자 ID")
    private Long senderId;

    @Schema(description = "타임스탬프")
    private LocalDateTime timestamp = LocalDateTime.now();
}
