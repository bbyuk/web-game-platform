package com.bb.webcanvasservice.presentation.chat.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "메세지 전송 요청 DTO")
public record SendMessageRequest(

        @Schema(description = "메세지 값")
        String value
) {}
