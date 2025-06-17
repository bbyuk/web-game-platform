package com.bb.webcanvasservice.game.domain.model;

/**
 * 게임에서 얻을 수 있는 스코어의 종류
 */
public enum ScoreType {
    /**
     * 정답을 맞힘
     */
    CORRECT_ANSWER(10),
    /**
     * 다른 유저가 정답을 맞힐 수 있도록 그림을 잘 그림
     */
    GOOD_DRAWING(5);

    private final int score;

    ScoreType(int score) {
        this.score = score;
    }
}
