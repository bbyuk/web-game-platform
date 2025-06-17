package com.bb.webcanvasservice.user.presentation.response;

import com.bb.webcanvasservice.user.domain.model.UserState;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유저 상태 정보 조회 API 응답 DTO")
public record UserStateInfoResponse(
        
        @Schema(description = "유저 상태")
        UserState state
) {
}
