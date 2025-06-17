package com.bb.webcanvasservice.game.domain.event;

import com.bb.webcanvasservice.common.event.ApplicationEvent;

/**
 * 게임 세션에 참여한 모든 유저들이 로드되었을 때 발행할 이벤트
 */
public class AllUserInGameSessionLoadedEvent extends ApplicationEvent {
    private final Long gameSessionId;
    private final Long gameRoomId;

    public AllUserInGameSessionLoadedEvent(Long gameSessionId, Long gameRoomId) {
        super("SESSION/ALL_USER_LOADED");
        this.gameSessionId = gameSessionId;
        this.gameRoomId = gameRoomId;
    }

    public Long getGameSessionId() {
        return gameSessionId;
    }

    public Long getGameRoomId() {
        return gameRoomId;
    }
}
