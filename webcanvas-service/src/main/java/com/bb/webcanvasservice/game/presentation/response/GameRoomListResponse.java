package com.bb.webcanvasservice.game.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "게임 방 조회 API의 응답 DTO")
public record GameRoomListResponse(
        @Schema(description = "게임 방 요약 정보 목록")
        List<GameRoomInfoResponse> roomList
) {}