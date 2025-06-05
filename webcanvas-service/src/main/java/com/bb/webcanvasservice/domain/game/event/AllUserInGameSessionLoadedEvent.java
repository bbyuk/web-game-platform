package com.bb.webcanvasservice.domain.game.event;

import com.bb.webcanvasservice.common.event.ApplicationEvent;
import lombok.Getter;

/**
 * 게임 세션에 참여한 모든 유저들이 로드되었을 때 발행할 이벤트
 */
@Getter
public class AllUserInGameSessionLoadedEvent extends ApplicationEvent {
    private final Long gameRoomId;
    private final Long gameSessionId;

    public AllUserInGameSessionLoadedEvent(Long gameRoomId, Long gameSessionId) {
        super("SESSION/ALL_USER_LOADED");
        this.gameRoomId = gameRoomId;
        this.gameSessionId = gameSessionId;
    }
}
