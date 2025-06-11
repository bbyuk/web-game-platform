package com.bb.webcanvasservice.domain.game.entity;

import com.bb.webcanvasservice.common.entity.BaseEntity;
import com.bb.webcanvasservice.domain.game.enums.GameTurnState;
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
public class GameTurn extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "id")
    /**
     * 게임 턴 ID
     */
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_session_id")
    /**
     *
     */
    private GameSession gameSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drawer_id")
    /**
     * 해당 턴에 그림을 그릴 차례인 유저
     */
    private UserJpaEntity drawer;

    @Column(name = "answer")
    /**
     * 해당 턴의 정답
     */
    private String answer;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "correct_answerer_id", nullable = true)
    /**
     * 정답을 맞힌 유저
     */
    private UserJpaEntity correctAnswerer;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private GameTurnState state;

    public GameTurn(GameSession gameSession, UserJpaEntity drawer, String answer) {
        this.gameSession = gameSession;
        this.drawer = drawer;
        this.answer = answer;
        this.state = GameTurnState.ACTIVE;
    }

    /**
     * 정답을 맞혔을을 때 호출
     * @param user
     */
    public void answeredCorrectlyBy(UserJpaEntity user) {
        correctAnswerer = user;
        this.state = GameTurnState.ANSWERED;
    }

    /**
     * 정답을 맟히지 못하고 제한 시간이 넘은 경우 호출
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

    /**
     * 종료 시각을 계산해 리턴한다.
     * Seconds
     * @return 게임 턴 Entity의 만료 시각
     */
    public LocalDateTime getExpiration() {
        return createdAt.plus(gameSession.getTimePerTurn(), ChronoUnit.SECONDS);
    }
}
