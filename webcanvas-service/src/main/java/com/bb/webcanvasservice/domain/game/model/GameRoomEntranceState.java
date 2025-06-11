package com.bb.webcanvasservice.domain.game.model;

import java.util.List;

/**
 * 게임 방 입장 Entity 의 상태 코드
 */
public enum GameRoomEntranceState {

    /**
     * 대기 상태
     */
    WAITING,


    /**
     * 게임 플레이중
     */
    PLAYING,

    /**
     * 퇴장
     */
    EXITED;

    public static final List<GameRoomEntranceState> entered = List.of(WAITING, PLAYING);
}
