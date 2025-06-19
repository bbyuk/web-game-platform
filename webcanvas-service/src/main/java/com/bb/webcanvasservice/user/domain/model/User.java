package com.bb.webcanvasservice.user.domain.model;

/**
 * webcanvas 게임 내에서의 유저 도메인 모델
 * 유저의 기준 정보는 플랫폼 서비스에서 관리하며, 게임 플레이에 필요한 유저 식별 정보만을 담당한다.
 */
public class User {

    /**
     * 유저 식별자
     */
    private final Long id;

    /**
     * 서버에서 유저 등록시 생성된 유저 fingerprint
     */
    private final String fingerprint;

    /**
     * 발급된 accessToken이 만료될 경우 refresh를 위한 토큰
     */
    private String refreshToken;

    /**
     * 유저 상태
     */
    private UserState state;

    public User(Long id, String fingerprint, UserState state, String refreshToken) {
        this.id = id;
        this.fingerprint = fingerprint;
        this.state = state;
        this.refreshToken = refreshToken;
    }


    public static User create(String fingerprint) {
        return new User(null, fingerprint, UserState.IN_LOBBY, null);
    }

    /**
     * 유저에게 할당된 refreshToken을 업데이트하여 리턴한다.
     * @param refreshToken
     */
    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    /**
     * 유저 상태를 변경한다.
     * @param state
     */
    public void changeState(UserState state) {
        this.state = state;
    }

    /**
     * 유저 상태를 방 안으로 변경한다.
     */
    public void moveToRoom() {
        this.state = UserState.IN_ROOM;
    }

    /**
     * 현재 플레이중인 게임을 완료하고 게임 방으로 돌아간다.
     */
    public void endGameAndResetToRoom() {
        if (this.state == UserState.IN_GAME) {
            this.state = UserState.IN_ROOM;
        }
    }

    /**
     * 매핑을 위한 getter 모음
     */
    public Long getId() {
        return id;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public UserState getState() {
        return state;
    }
}
