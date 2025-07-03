package com.bb.webcanvasservice.game.domain.model.session;

/**
 * 게임 플레이어 상태 코드
 */
public enum GamePlayerState {
    /**
     * 시작
     */
    INIT,
    /**
     * 로드 됨
     */
    LOADED,
    /**
     * 플레이 중
     */
    PLAYING,
    /**
     * 완료됨
     */
    INACTIVE
}
