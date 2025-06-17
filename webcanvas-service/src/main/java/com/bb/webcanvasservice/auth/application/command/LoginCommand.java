package com.bb.webcanvasservice.auth.application.command;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 요청 command")
public record LoginCommand(

        @Schema(description = "서버에서 유저 생성시 함께 생성된 클라이언트 fingerprint")
        String fingerprint
) {
}
