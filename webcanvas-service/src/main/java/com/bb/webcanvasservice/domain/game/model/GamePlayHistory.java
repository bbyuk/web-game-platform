package com.bb.webcanvasservice.domain.game.model;

/**
 * 유저별 게임 플레이 이력
 */
public class GamePlayHistory {

    /**
     * 게임을 플레이한 유저 ID
     */
    private final Long userId;

    /**
     * 게임을 플레이한 세션 ID
     */
    private final Long gameSessionId;

    public GamePlayHistory(Long userId, Long gameSessionId) {
        this.userId = userId;
        this.gameSessionId = gameSessionId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getGameSessionId() {
        return gameSessionId;
    }
}
