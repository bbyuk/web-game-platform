package com.bb.webcanvasservice.game.infrastructure.persistence.entity;

import com.bb.webcanvasservice.game.domain.model.session.GameTurnState;
import com.bb.webcanvasservice.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 게임 세션에 종속된 게임 턴
 */
@Getter
@Entity
@ToString
@Table(name = "game_turns")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameTurnJpaEntity extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "id")
    /**
     * 게임 턴 ID
     */
    private Long id;

    @JoinColumn(name = "game_session_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private GameSessionJpaEntity gameSessionEntity;

    @Column(name = "drawer_id")
    /**
     * 해당 턴에 그림을 그릴 차례인 유저
     */
    private Long drawerId;

    @Column(name = "answer")
    /**
     * 해당 턴의 정답
     */
    private String answer;

    @Column(name = "correct_answerer_id", nullable = true)
    /**
     * 정답을 맞힌 유저
     */
    private Long correctAnswererId;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private GameTurnState state;

}
