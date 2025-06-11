package com.bb.webcanvasservice.presentation.user.response;

import com.bb.webcanvasservice.domain.user.model.UserStateCode;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유저 상태 정보 조회 API 응답 DTO")
public record UserStateInfoResponse(
        
        @Schema(description = "유저 상태")
        UserStateCode state
) {
}
