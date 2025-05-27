package com.bb.webcanvasservice.domain.game.enums;

/**
 * 게임 턴 Entity의 상태 코드
 */
public enum GameTurnState {
    /**
     * 정답을 맞힘
     */
    ANSWERED,

    /**
     * 진행중
     */
    ACTIVE,

    /**
     * 정답을 맞히지 못하고 PASS됨
     */
    PASSED
}
