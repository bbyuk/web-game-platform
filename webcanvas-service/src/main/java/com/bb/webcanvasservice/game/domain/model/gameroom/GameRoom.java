package com.bb.webcanvasservice.game.domain.model.gameroom;

import java.util.List;

/**
 * 게임 방을 나타내는 도메인 모델
 */
public class GameRoom {

    /**
     * 게임 방 ID
     */
    private final Long id;


    /**
     * 게임 방의 입장 코드
     */
    private final String joinCode;

    /**
     * 게임 방 상태
     */
    private GameRoomState state;

    /**
     * 현재 게임 방에서 진행중인 게임 세션
     */
    private GameSession currentGameSession;

    public GameRoom(Long id, String joinCode, GameRoomState state) {
        this.id = id;
        this.joinCode = joinCode;
        this.state = state;
    }

    /**
     * 새 게임 방을 생성해 리턴한다.
     * @return 게임 방
     */
    public static GameRoom create(String joinCode) {
        return new GameRoom(null, joinCode, GameRoomState.WAITING);
    }

    /**
     * 게임 방을 플레이 상태로 변경한다.
     */
    public void changeStateToPlay() {
        this.state = GameRoomState.PLAYING;
    }

    /**
     * 게임 세션이 종료된 후 WAITING 상태로 방 상태를 리셋한다.
     */
    public void resetGameRoomState() {
        this.state = GameRoomState.WAITING;
    }

    public boolean isWaiting() {
        return this.state == GameRoomState.WAITING;
    }

    /**
     * 게임 방 상태를 close한다.
     */
    public void close() {
        this.state = GameRoomState.CLOSED;
    }

    public GameSession getCurrentGameSession() {
        return currentGameSession;
    }

    public Long getId() {
        return id;
    }

    public String getJoinCode() {
        return joinCode;
    }

    public GameRoomState getState() {
        return state;
    }
}
