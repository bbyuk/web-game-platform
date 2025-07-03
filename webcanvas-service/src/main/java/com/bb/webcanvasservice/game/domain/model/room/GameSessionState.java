package com.bb.webcanvasservice.game.domain.model.room;

import java.util.List;

/**
 * 게임 세션의 상태
 */
public enum GameSessionState {
    LOADING,
    PLAYING,
    COMPLETED;

    public final static List<GameSessionState> active = List.of(GameSessionState.PLAYING, GameSessionState.LOADING);
}
