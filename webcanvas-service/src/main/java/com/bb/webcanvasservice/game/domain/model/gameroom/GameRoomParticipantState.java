package com.bb.webcanvasservice.game.domain.model.gameroom;

import java.util.List;

/**
 * 게임 방 입장 Entity 의 상태 코드
 */
public enum GameRoomParticipantState {

    /**
     * 대기 상태
     */
    WAITING,

    /**
     * 게임 방 내
     */
    IN_ROOM,

    /**
     * 게임 플레이중
     */
    PLAYING,

    /**
     * 퇴장
     */
    EXITED;

    public static final List<GameRoomParticipantState> joined = List.of(IN_ROOM, PLAYING);
}
