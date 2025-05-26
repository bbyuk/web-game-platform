package com.bb.webcanvasservice.domain.game.enums;

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
    EXITED
}
