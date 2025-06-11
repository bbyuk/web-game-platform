package com.bb.webcanvasservice.infrastructure.persistence.game.entity;

import com.bb.webcanvasservice.common.entity.BaseEntity;
import com.bb.webcanvasservice.domain.game.model.GameTurnState;
import com.bb.webcanvasservice.infrastructure.persistence.user.entity.UserJpaEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 게임 세션에 종속된 게임 턴
 */
@Getter
@Entity
@ToString
@Table(name = "game_turns")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameTurnJpaEntity extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "id")
    /**
     * 게임 턴 ID
     */
    private Long id;

    @Column(name = "game_session_id")
    /**
     *
     */
    private Long gameSessionId;

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

    public GameTurnJpaEntity(Long gameSessionId, Long drawerId, String answer) {
        this.gameSessionId = gameSessionId;
        this.drawerId = drawerId;
        this.answer = answer;
        this.state = GameTurnState.ACTIVE;
    }

}
