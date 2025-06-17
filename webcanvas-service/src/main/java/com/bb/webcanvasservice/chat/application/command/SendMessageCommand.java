package com.bb.webcanvasservice.chat.application.command;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "채팅 메세지 전송 애플리케이션 서비스 입력용 DTO")
public record SendMessageCommand(

        @Schema(description = "브로드캐스팅 대상 게임 방 ID")
        Long gameRoomId,

        @Schema(description = "전송자 ID")
        Long senderId,

        @Schema(description = "메세지 값")
        String message
) {
}
