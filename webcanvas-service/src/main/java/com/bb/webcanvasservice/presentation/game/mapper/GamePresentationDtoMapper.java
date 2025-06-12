package com.bb.webcanvasservice.presentation.game.mapper;


import com.bb.webcanvasservice.application.game.dto.*;
import com.bb.webcanvasservice.presentation.game.response.*;

/**
 * application layer dto -> presentation layer dto mapping
 * 게임 및 게임 방 정보 API 응답
 */
public class GamePresentationDtoMapper {
    public static GameRoomEntranceResponse toGameRoomEntranceResponse(GameRoomEntranceDto dto) {
        return new GameRoomEntranceResponse(dto.gameRoomEntranceId(), dto.gameRoomId());
    }

    public static GameRoomListResponse toGameRoomListResponse(GameRoomListDto dto) {
        return new GameRoomListResponse(dto.roomList().stream().map(GamePresentationDtoMapper::toGameRoomInfoResponse).toList());
    }

    public static GameRoomInfoResponse toGameRoomInfoResponse(GameRoomInfoDto dto) {
        return new GameRoomInfoResponse(dto.gameRoomId(), dto.capacity(), dto.enterCount(), dto.joinCode());
    }

    public static GameRoomEntranceDetailInfoResponse toGameRoomEntranceDetailInfoResponse(GameRoomEntranceDetailInfoDto dto) {
        return new GameRoomEntranceDetailInfoResponse(
                dto.gameRoomId(),
                dto.gameRoomEntranceId(),
                dto.enteredUsers().stream().map(GamePresentationDtoMapper::toEnteredUserInfoResponse).toList(),
                dto.gameRoomState().name(),
                toEnteredUserInfoResponse(dto.requesterUserSummary())
        );
    }

    public static EnteredUserInfoResponse toEnteredUserInfoResponse(EnteredUserInfoDto dto) {
        return new EnteredUserInfoResponse(dto.userId(), dto.color(), dto.nickname(), dto.role().name(), dto.ready());
    }

    public static GameTurnResponse toGameTurnResponse(GameTurnDto dto) {
        return new GameTurnResponse(dto.drawerId(), dto.answer(), dto.expiration());
    }

    public static GameSessionResponse toGameSessionResponse(GameSessionDto dto) {
        return new GameSessionResponse(dto.gameSessionId(), dto.state().name(), dto.timePerTurn(), dto.currentTurnIndex(), dto.turnCount());
    }
}
