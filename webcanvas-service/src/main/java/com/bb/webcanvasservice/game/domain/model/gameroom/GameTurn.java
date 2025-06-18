package com.bb.webcanvasservice.game.domain.model.gameroom;

import java.time.LocalDateTime;

/**
 * 게임 세션에 종속된 게임 턴 도메인 모델
 */
public class GameTurn {

    /**
     * 게임 턴 ID
     */
    private final Long id;

    /**
     * 게임 세션
     */
    private final Long gameSessionId;

    /**
     * 해당 턴에 그림을 그릴 차례인 유저 ID
     */
    private final Long drawerId;

    /**
     * 해당 턴의 정답
     */
    private final String answer;

    private final LocalDateTime startedAt;

    /**
     * 정답을 맞힌 유저 ID
     */
    private Long correctAnswererId;

    private GameTurnState state;

    public GameTurn(Long id, Long gameSessionId, Long drawerId, String answer, LocalDateTime startedAt, Long correctAnswererId, GameTurnState state) {
        this.id = id;
        this.gameSessionId = gameSessionId;
        this.drawerId = drawerId;
        this.answer = answer;
        this.correctAnswererId = correctAnswererId;
        this.state = state;
        this.startedAt = startedAt;
    }

    public static GameTurn createNewGameTurn(Long gameSessionId, Long drawerId, String answer) {
        return new GameTurn(null, gameSessionId, drawerId, answer, LocalDateTime.now(), null, GameTurnState.ACTIVE);
    }

    /**
     * 정답을 맞혔을을 때 호출
     * @param userId 유저 ID
     */
    public void answeredCorrectlyBy(Long userId) {
        this.correctAnswererId = userId;
        this.state = GameTurnState.ANSWERED;
    }

    /**
     * 정답을 맟히지 못하고 제한 시간이 넘은 경우 턴 상태를 PASS로 변경한다.
     */
    public void pass() {
        if (isActive()) {
            this.state = GameTurnState.PASSED;
        }
    }

    /**
     * 현재 활성화 되어 있는 턴인지 여부를 체크한다.
     * @return 활성화 여부
     */
    public boolean isActive() {
        return this.state == GameTurnState.ACTIVE;
    }

    /**
     * 대상 턴의 정답이 맞는지 체크한다.
     * @param answer
     * @return
     */
    public boolean isAnswer(String answer) {
        return this.answer.equals(answer);
    }

    public Long getId() {
        return id;
    }

    public Long getGameSessionId() {
        return gameSessionId;
    }

    public String getAnswer() {
        return answer;
    }

    public Long getCorrectAnswererId() {
        return correctAnswererId;
    }

    public Long getDrawerId() {
        return drawerId;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public GameTurnState getState() {
        return state;
    }
}
