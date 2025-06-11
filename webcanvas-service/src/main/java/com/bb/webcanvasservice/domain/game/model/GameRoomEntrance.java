package com.bb.webcanvasservice.domain.game.model;

import static com.bb.webcanvasservice.domain.game.model.GameRoomEntranceRole.HOST;

/**
 * 유저의 게임 방 입장을 나타내는 도메인 모델
 */
public class GameRoomEntrance {

    /**
     * 게임 방 입장 ID
     */
    private final Long id;

    /**
     * 입장한 게임 방 ID
     */
    private final Long gameRoomId;

    /**
     * 입장한 유저
     */
    private final Long userId;

    /**
     * 게임 방 내에서의 유저 닉네임 -> 사전에서 랜덤으로 찾아와 조합할 예정
     */
    private String nickname;

    /**
     * 게임 방 내에서의 역할
     */
    private GameRoomEntranceRole role;

    /**
     * 게임 방 입장 기록 상태
     */
    private GameRoomEntranceState state;

    /**
     * 준비 여부
     */
    private boolean ready;

    public boolean isReady() {
        if (this.role == HOST) {
            return true;
        }

        return ready;
    }

    public GameRoomEntrance(Long id, Long gameRoomId, Long userId, GameRoomEntranceState state, String nickname, GameRoomEntranceRole role, boolean ready) {
        this.id = id;
        this.gameRoomId = gameRoomId;
        this.userId = userId;
        this.state = state;
        this.nickname = nickname;
        this.role = role;
        this.ready = ready;
    }

    /**
     * 게임 입장 Entity를 exit 처리한다.
     */
    public void exit() {
        this.state = GameRoomEntranceState.EXITED;
    }

    /**
     * 역할을 변경한다.
     * @param gameRoomEntranceRole 게임 방 입장 역할
     */
    public void changeRole(GameRoomEntranceRole gameRoomEntranceRole) {
        this.role = gameRoomEntranceRole;
        if (this.role == HOST) {
            this.ready = true;
        }
    }

    /**
     * 레디 상태를 바꾼다.
     */
    public void changeReady(boolean ready) {
        this.ready = ready;
    }

    /**
     * GameRoomEntranceState를 변경한다.
     * @param state
     */
    public void changeState(GameRoomEntranceState state) {
        this.state = state;
    }

    /**
     * 게임 방 입장 정보 entity의 상태를 초기화하고, 유저 entity의 상태 초기화를 요청한다.
     */
    public void resetGameRoomEntranceInfo() {
        this.state = GameRoomEntranceState.WAITING;
    }

    /**
     * 유저가 호스트인지 체크한다.
     * @return
     */
    public boolean isHost() {
        return this.role == HOST;
    }

    public Long getId() {
        return id;
    }

    public Long getGameRoomId() {
        return gameRoomId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }

    public GameRoomEntranceState getState() {
        return state;
    }

    public GameRoomEntranceRole getRole() {
        return role;
    }

}
