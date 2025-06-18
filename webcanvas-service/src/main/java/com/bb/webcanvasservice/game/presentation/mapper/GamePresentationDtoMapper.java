package com.bb.webcanvasservice.game.presentation.mapper;


import com.bb.webcanvasservice.game.application.dto.*;
import com.bb.webcanvasservice.game.presentation.response.*;

/**
 * application layer dto -> presentation layer dto mapping
 * 게임 및 게임 방 정보 API 응답
 */
public class GamePresentationDtoMapper {
    public static GameRoomJoinResponse toGameRoomJoinResponse(GameRoomJoinDto dto) {
        return new GameRoomJoinResponse(dto.gameRoomId(), dto.gameRoomParticipantId());
    }

    public static GameRoomListResponse toGameRoomListResponse(GameRoomListDto dto) {
        return new GameRoomListResponse(dto.roomList().stream().map(GamePresentationDtoMapper::toGameRoomInfoResponse).toList());
    }

    public static GameRoomInfoResponse toGameRoomInfoResponse(GameRoomInfoDto dto) {
        return new GameRoomInfoResponse(dto.gameRoomId(), dto.capacity(), dto.enterCount(), dto.joinCode());
    }

    public static GameRoomJoinDetailInfoResponse toGameRoomJoinDetailInfoResponse(GameRoomJoinDetailInfoDto dto) {
        return new GameRoomJoinDetailInfoResponse(
                dto.gameRoomId(),
                dto.gameRoomParticipantId(),
                dto.joinedUsers().stream().map(GamePresentationDtoMapper::toEnteredUserInfoResponse).toList(),
                dto.gameRoomState().name(),
                toEnteredUserInfoResponse(dto.requesterUserSummary())
        );
    }

    public static JoinedUserInfoResponse toEnteredUserInfoResponse(JoinedUserInfoDto dto) {
        return new JoinedUserInfoResponse(dto.userId(), dto.color(), dto.nickname(), dto.role().name(), dto.ready());
    }

    public static GameTurnResponse toGameTurnResponse(GameTurnDto dto) {
        return new GameTurnResponse(dto.drawerId(), dto.answer(), dto.expiration());
    }

    public static GameSessionResponse toGameSessionResponse(GameSessionDto dto) {
        return new GameSessionResponse(dto.gameSessionId(), dto.state().name(), dto.timePerTurn(), dto.currentTurnIndex(), dto.turnCount());
    }
}
