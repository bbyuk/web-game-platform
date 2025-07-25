package com.bb.webcanvasservice.game.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "입장한 게임 방 정보 조회 API 응답 DTO")
public record GameRoomJoinDetailInfoResponse(
        @Schema(description = "게임 방 ID", example = "512")
        Long gameRoomId,

        @Schema(description = "게임 방 입장자 ID", example = "2231")
        Long gameRoomParticipantId,

        @Schema(description = "현재 게임방에 접속해있는 유저들의 정보")
        List<JoinedUserInfoResponse> enteredUsers,

        @Schema(description = "현재 입장한 게임 방의 상태")
        String gameRoomState,

        @Schema(description = "요청한 유저의 정보")
        JoinedUserInfoResponse requesterUserSummary
) {}
