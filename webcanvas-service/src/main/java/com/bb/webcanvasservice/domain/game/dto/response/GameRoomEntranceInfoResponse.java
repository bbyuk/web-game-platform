package com.bb.webcanvasservice.domain.game.dto.response;

import com.bb.webcanvasservice.domain.game.enums.GameRoomRole;
import com.bb.webcanvasservice.domain.game.enums.GameRoomState;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "입장한 게임 방 정보 조회 API 응답 DTO")
public record GameRoomEntranceInfoResponse(
        @Schema(description = "게임 방 ID", example = "512")
        Long gameRoomId,

        @Schema(description = "게임 방 입장 ID", example = "2231")
        Long gameRoomEntranceId,

        @Schema(description = "현재 게임방에 접속해있는 유저들의 정보")
        List<EnteredUserSummary> enteredUsers,

        @Schema(description = "현재 입장한 게임 방의 상태")
        GameRoomState gameRoomState,

        @Schema(description = "요청한 유저의 정보")
        EnteredUserSummary requesterUserSummary
) {
    /**
     * 게임 방에 입장한 유저 정보 DTO
     * @param userId
     */
    @Schema(description = "게임 방에 입장한 유저 정보 DTO")
    public record EnteredUserSummary (

            @Schema(description = "유저 ID", example = "512")
            Long userId,

            @Schema(description = "방 노출 컬러", example = "#1e9000")
            String color,

            @Schema(description = "자동 생성된 게임 방 내 유저 닉네임", example = "고매한 여우")
            String nickname,

            @Schema(description = "게임 방 내에서의 ROLE")
            GameRoomRole role,

            @Schema(description = "레디 여부")
            boolean ready

    ) {};
}
