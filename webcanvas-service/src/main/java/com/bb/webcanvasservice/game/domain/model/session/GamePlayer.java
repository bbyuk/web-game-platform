package com.bb.webcanvasservice.game.domain.model.session;

/**
 * 게임 세션 참여자
 */
public class GamePlayer {
    /**
     * 게임 방 입장 유저 ID
     */
    private final Long id;

    /**
     * 입장한 게임 방
     */
    private Long gameSessionId;

    /**
     * 입장한 유저 ID
     */
    private final Long userId;

    /**
     * 게임 방 내에서의 유저 닉네임 -> 사전에서 랜덤으로 찾아와 조합할 예정
     */
    private final String nickname;

    private GamePlayerState state;

    public GamePlayer(Long id, Long gameSessionId, Long userId, String nickname, GamePlayerState state) {
        this.id = id;
        this.gameSessionId = gameSessionId;
        this.userId = userId;
        this.nickname = nickname;
        this.state = state;
    }

    public static GamePlayer create(Long gameSessionId, Long userId, String nickname) {
        return new GamePlayer(null, gameSessionId, userId, nickname, GamePlayerState.INIT);
    }

    // ===================================================
    // ====================== getter =====================
    // ===================================================

    public Long id() { return id; }
    public Long userId() {
        return userId;
    }
    public Long gameSessionId() { return gameSessionId; }
    public String nickname() { return nickname; }
    public GamePlayerState state() { return state; }

    // ===================================================
    // ====================== getter =====================
    // ===================================================

    /**
     * 로딩중인지 여부를 리턴한다.
     * @return 로딩 여부
     */
    public boolean isLoaded() {
        return this.state == GamePlayerState.LOADED;
    }

    /**
     * 세션에 player를 로드한다.
     */
    public void load() {
        if (this.state() == GamePlayerState.INIT) {
            this.state = GamePlayerState.LOADED;
        }
    }

    /**
     * 로딩을 마치고 플레잉 상태로 변경한다.
     */
    public void changeStateToPlaying() {
        this.state = GamePlayerState.PLAYING;
    }

    /**
     * 게임 플레이어의 상태를 deactivate한다.
     */
    public void deactivate() {
        this.state = GamePlayerState.INACTIVE;
    }

    /**
     * 게임 플레이어가 플레이중인지 체크한다.
     * @return
     */
    public boolean isPlaying() {
        return this.state == GamePlayerState.PLAYING;
    }
}
