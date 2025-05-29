package com.bb.webcanvasservice.domain.user.dto.response;

import com.bb.webcanvasservice.domain.user.enums.UserStateCode;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유저 상태 정보 조회 API 응답 D썌")
public record UserStateInfo(
        
        @Schema(description = "유저 상태")
        UserStateCode state
) {
}
