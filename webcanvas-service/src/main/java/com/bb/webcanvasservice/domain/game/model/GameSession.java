package com.bb.webcanvasservice.domain.game.model;

/**
 * 게임의 세션을 나타내는 도메인 모델
 */
public class GameSession {

    /**
     * 게임 세션 ID
     */
    private final Long id;

    /**
     * 게임 방
     */
    private final GameRoom gameRoom;

    /**
     * 게임 턴 수
     */
    private final int turnCount;

    /**
     * 턴 당 시간
     */
    private final int timePerTurn;

    /**
     * 게임 세션의 상태
     */
    private GameSessionState state;



    public GameSession(Long id, GameRoom gameRoom, int turnCount, int timePerTurn, GameSessionState state) {
        this.id = id;
        this.gameRoom = gameRoom;
        this.turnCount = turnCount;
        this.timePerTurn = timePerTurn;
        this.state = state;
    }

    /**
     * 게임 세션을 종료하고 게임 세션과 연관되어 있는 게임방 객체의 상태 리셋을 요청한다.
     */
    public void end() {
        state = GameSessionState.COMPLETED;
        gameRoom.resetGameRoomState();
    }

    /**
     * 게임 세션을 시작한다.
     */
    public void start() {
        state = GameSessionState.PLAYING;
    }

    /**
     * 게임 세션이 현재 플레이 중인지 확인한다.
     * @return 게임 세션 현재 플레이 여부
     */
    public boolean isPlaying() {
        return state == GameSessionState.PLAYING;
    }

    /**
     * 게임 세션이 활성상태인지 확인한다.
     * @return 게임 세션 현재 활성 여부
     */
    public boolean isActive() {
        return state == GameSessionState.PLAYING || state == GameSessionState.LOADING;
    }

    /**
     * 게임 종료인지 여부를 체크한다.
     * @return 게임 종료 여부
     */
    public boolean isEnd() {
        return state == GameSessionState.COMPLETED;
    }

    /**
     * 해당 세션에 할당된 턴 갯수
     * @return
     */
    public int getTurnCount() {
        return turnCount;
    }

    /**
     * 해당 세션에서 설정된 게임 턴당 시간
     * @return
     */
    public int getTimePerTurn() {
        return timePerTurn;
    }
}
