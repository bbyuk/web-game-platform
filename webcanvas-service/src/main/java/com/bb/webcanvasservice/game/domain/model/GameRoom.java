package com.bb.webcanvasservice.game.domain.model;

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

    public GameRoom(Long id, String joinCode, GameRoomState state) {
        this.id = id;
        this.joinCode = joinCode;
        this.state = state;
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
