package com.bb.webcanvasservice.user.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "application layer user dto")
public record UserDto(
        @Schema(description = "유저 식별 ID", example = "15")
        Long userId,
        @Schema(description = "클라이언트로부터 얻은 Fingerprint로, 유저 생성 이후에는 유저의 Fingerprint 역할을 한다.")
        String fingerprint
) {}
