package com.bb.webcanvasservice.domain.user.model;

/**
 * 유저 상태 코드
 */
public enum UserState {
    /**
     * 로비에 존재
     */
    IN_LOBBY,
    /**
     * 방에 입장하고 Waiting 상태
     */
    IN_ROOM,
    /**
     * 게임 진행중
     */
    IN_GAME
}
