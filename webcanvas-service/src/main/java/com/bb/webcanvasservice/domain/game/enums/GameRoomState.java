package com.bb.webcanvasservice.domain.game.enums;


/**
 * 게임 방의 상태를 나타내는 상태 코드 enum
 */
public enum GameRoomState {
    /**
     * 방이 닫힌 상태
     * 더 이상 사용되지 않음
     */
    CLOSED,
    /**
     * 방이 활성 상태로, 게임이 시작되지 않은 상태.
     */
    WAITING,
    /**
     * 현재 게임이 진행중인 상태
     */
    PLAYING
}
