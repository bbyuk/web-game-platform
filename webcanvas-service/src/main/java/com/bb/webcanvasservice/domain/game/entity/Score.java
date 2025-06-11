package com.bb.webcanvasservice.domain.game.entity;

import com.bb.webcanvasservice.common.entity.BaseEntity;
import com.bb.webcanvasservice.domain.game.enums.ScoreType;
import com.bb.webcanvasservice.infrastructure.persistence.user.entity.UserJpaEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 턴별로 획득한 점수 엔티티
 */
@Entity
@Getter
@Table(name = "scores")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Score extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "id")
    /**
     * 스코어 ID
     */
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    /**
     * 점수 엔티티의 오너
     */
    private UserJpaEntity owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scored_turn_id")
    /**
     * 스코어 집계 대상 게임 턴
     */
    private GameTurn scoredTurn;

    @Column(name = "message")
    /**
     * 스코어 값
     */
    private int value;

    @Column(name = "score_type")
    @Enumerated(EnumType.STRING)
    private ScoreType type;

    public Score(UserJpaEntity scorer, GameTurn scoredTurn, int value, ScoreType type) {
        this.owner = scorer;
        this.scoredTurn = scoredTurn;
        this.value = value;
        this.type = type;
    }
}
