package com.bb.webcanvasservice.domain.game.dto.request;

/**
 * 게임 방에 입장한 유저의 레디 상태 변경 요청 DTO
 */
public record GameRoomReadyUpdateRequest(
        boolean ready
) {
}
