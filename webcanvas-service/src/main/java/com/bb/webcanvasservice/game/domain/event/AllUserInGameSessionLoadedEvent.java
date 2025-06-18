package com.bb.webcanvasservice.game.domain.event;

import com.bb.webcanvasservice.common.event.ApplicationEvent;

/**
 * 게임 세션에 참여한 모든 유저들이 로드되었을 때 발행할 이벤트
 */
public class AllUserInGameSessionLoadedEvent extends ApplicationEvent {
    private final Long gameSessionId;
    private final Long gameRoomId;
    private final int timePerTurn;

    public AllUserInGameSessionLoadedEvent(Long gameSessionId, Long gameRoomId, int timePerTurn) {
        super("SESSION/ALL_USER_LOADED");
        this.gameSessionId = gameSessionId;
        this.gameRoomId = gameRoomId;
        this.timePerTurn = timePerTurn;
    }

    public Long getGameSessionId() {
        return gameSessionId;
    }

    public Long getGameRoomId() {
        return gameRoomId;
    }

    public int getTimePerTurn() {
        return timePerTurn;
    }
}
