package com.bb.webcanvasservice.game.domain.model;

import com.bb.webcanvasservice.game.domain.model.session.GameTurn;

/**
 * 턴별로 획득한 점수 도메인 모델
 */
public class Score {

    /**
     * 스코어 ID
     */
    private final Long id;

    /**
     * 점수 엔티티의 오너 ID
     */
    private final Long ownerId;

    /**
     * 스코어 집계 대상 게임 턴
     */
    private final GameTurn scoredTurn;

    /**
     * 스코어 값
     */
    private final int value;

    /**
     * 점수 타입
     */
    private final ScoreType type;

    public Score(Long id, Long scorerId, GameTurn scoredTurn, int value, ScoreType type) {
        this.id = id;
        this.ownerId = scorerId;
        this.scoredTurn = scoredTurn;
        this.value = value;
        this.type = type;
    }
}
