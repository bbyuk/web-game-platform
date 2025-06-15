package com.bb.webcanvasservice.infrastructure.persistence.game.entity;

import com.bb.webcanvasservice.domain.game.model.ScoreType;
import com.bb.webcanvasservice.infrastructure.persistence.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 턴별로 획득한 점수 엔티티
 */
@Entity
@Getter
@Table(name = "scores")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ScoreJpaEntity extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "id")
    /**
     * 스코어 ID
     */
    private Long id;

    @Column(name = "owner_id")
    /**
     * 점수 엔티티의 오너
     */
    private Long ownerId;

    @Column(name = "scored_turn_id")
    /**
     * 스코어 집계 대상 게임 턴
     */
    private Long scoredTurnId;

    @Column(name = "message")
    /**
     * 스코어 값
     */
    private int value;

    @Column(name = "score_type")
    @Enumerated(EnumType.STRING)
    private ScoreType type;

}
