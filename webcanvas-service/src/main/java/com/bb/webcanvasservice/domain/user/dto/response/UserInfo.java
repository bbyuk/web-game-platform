package com.bb.webcanvasservice.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 유저 정보 응답 DTO
 * @param userId 유저 식별 ID
 * @param fingerprint
 */
@Schema(description = "유저 정보 응답 DTO")
public record UserInfo(
        @Schema(description = "유저 식별 ID", example = "15")
        Long userId,
        @Schema(description = "클라이언트 Fingerprint")
        String fingerprint
) {}
