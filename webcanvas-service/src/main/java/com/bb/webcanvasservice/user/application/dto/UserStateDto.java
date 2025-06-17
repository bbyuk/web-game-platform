package com.bb.webcanvasservice.user.application.dto;

import com.bb.webcanvasservice.user.domain.model.UserState;
import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "application layer dto 유저 상태 정보")
public record UserStateDto(
        
        @Schema(description = "유저 상태 코드")
        UserState state
) {
}
