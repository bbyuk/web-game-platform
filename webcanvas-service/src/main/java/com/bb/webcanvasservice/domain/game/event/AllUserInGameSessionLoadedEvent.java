package com.bb.webcanvasservice.domain.game.event;

import com.bb.webcanvasservice.common.event.ApplicationEvent;
import lombok.Getter;

/**
 * 게임 세션에 참여한 모든 유저들이 로드되었을 때 발행할 이벤트
 */
@Getter
public class AllUserInGameSessionLoadedEvent extends ApplicationEvent {
    private final Long gameSessionId;
    private final Long gameRoomId;

    public AllUserInGameSessionLoadedEvent(Long gameSessionId, Long gameRoomId) {
        super("SESSION/ALL_USER_LOADED");
        this.gameSessionId = gameSessionId;
        this.gameRoomId = gameRoomId;
    }
}
