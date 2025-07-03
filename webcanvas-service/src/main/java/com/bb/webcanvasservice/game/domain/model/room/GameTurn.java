package com.bb.webcanvasservice.game.domain.model.room;

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

    private int duration;

    public GameTurn(Long id, Long gameSessionId, Long drawerId, String answer, LocalDateTime startedAt, Long correctAnswererId, GameTurnState state, int duration) {
        this.id = id;
        this.gameSessionId = gameSessionId;
        this.drawerId = drawerId;
        this.answer = answer;
        this.correctAnswererId = correctAnswererId;
        this.state = state;
        this.startedAt = startedAt;
        this.duration = duration;
    }

    public static GameTurn create(Long gameSessionId, Long drawerId, String answer, int duration) {
        return new GameTurn(null, gameSessionId, drawerId, answer, LocalDateTime.now(), null, GameTurnState.ACTIVE, duration);
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
     * 현재 활성화 되어 있는 턴인지 여부를 체크한다.
     * @return 활성화 여부
     */
    public boolean isActive() {
        return this.state == GameTurnState.ACTIVE;
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

    /**
     * 게임 턴이 완료되었는지 여부를 리턴한다.
     * @return
     */
    public boolean isCompleted() {
        return state == GameTurnState.PASSED || state == GameTurnState.ANSWERED;
    }

    /**
     * 턴의 만료 시간을 계산해 리턴한다.
     * @return 만료 시간
     */
    public LocalDateTime calculateExpiration() {
        return startedAt.plusSeconds(duration);
    }

    /**
     * 대상 턴의 정답이 맞는지 체크한다.
     * @param value 입력 값
     * @return 정답 여부
     */
    public boolean isAnswer(String value) {
        return this.answer.equals(answer);
    }

    /**
     * 대상 턴의 정답을 포함한 메세지인지 확인한다.
     * @param value 입력 값
     * @return 정답 포함 여부
     */
    public boolean containsAnswer(String value) {
        return value.contains(answer);
    }

    /**
     * 해당턴의 drawer인지 여부를 확인한다.
     * @param userId 유저 ID
     * @return drawer 여부
     */
    public boolean isDrawer(Long userId) {
        return drawerId.equals(userId);
    }

    /**
     * 정답 처리를 수행한다.
     * @param userId
     */
    public void markingAsCorrect(Long userId) {
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

        /**
         * TODO 이벤트 발행
         */
    }
}
