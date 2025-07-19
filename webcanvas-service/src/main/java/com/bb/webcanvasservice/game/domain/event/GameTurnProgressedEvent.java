package com.bb.webcanvasservice.game.domain.event;

import com.bb.webcanvasservice.domain.shared.event.ApplicationEvent;

/**
 * 게임 턴이 넘어갔을 때 발행되는 이벤트
 */
public class GameTurnProgressedEvent extends ApplicationEvent {

    /**
     * 게임 방 ID
     */
    private final Long gameRoomId;
    /**
     * 게임 세션 ID
     */
    private final Long gameSessionId;

    /**
     * 게임 턴 ID
     */
    private final Long gameTurnId;

    /**
     * 첫번째 턴 여부
     */
    private final boolean first;

    /**
     * 이전 턴 정답자 ID
     */
    private final Long prevTurnAnswererId;

    /**
     * 다음턴 시작 딜레이 초
     */
    private final int startDelaySeconds;

    public GameTurnProgressedEvent(Long gameRoomId, Long gameSessionId, Long gameTurnId, boolean isFirst, Long prevTurnAnswererId, int startDelaySeconds) {
        super("SESSION/TURN_PROGRESSED");
        this.gameRoomId = gameRoomId;
        this.gameSessionId = gameSessionId;
        this.gameTurnId = gameTurnId;
        this.first = isFirst;
        this.prevTurnAnswererId = prevTurnAnswererId;
        this.startDelaySeconds = startDelaySeconds;
    }

    public Long getGameRoomId() {
        return gameRoomId;
    }

    public Long getGameSessionId() {
        return gameSessionId;
    }

    public Long getGameTurnId() {
        return gameTurnId;
    }

    public boolean isFirst() {
        return first;
    }

    public Long getPrevTurnAnswererId() {
        return prevTurnAnswererId;
    }

    public int getStartDelaySeconds() {
        return startDelaySeconds;
    }
}
