package com.bb.webcanvasservice.domain.game.entity;

import com.bb.webcanvasservice.domain.game.enums.GameTurnState;
import com.bb.webcanvasservice.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 게임 세션에 종속된 게임 턴
 */
@Getter
@Entity
@Table(name = "game_turns")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameTurn {

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
    private User drawer;

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
    private User correctAnswerer;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private GameTurnState state;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "scoredTurn")
    /**
     * 이번 턴에 획득한 점수 목록
     */
    private List<Score> scores = new ArrayList<>();

    public GameTurn(GameSession gameSession, User drawer, String answer) {
        this.gameSession = gameSession;
        this.gameSession.getGameTurns().add(this);
        this.drawer = drawer;
        this.answer = answer;
        this.state = GameTurnState.ACTIVE;
    }

    /**
     * 정답을 맞혔을을 때 호출
     * @param user
     */
    public void answeredCorrectlyBy(User user) {
        correctAnswerer = user;
        this.state = GameTurnState.ANSWERED;
    }

    /**
     * 정답을 맟히지 못하고 제한 시간이 넘은 경우 호출
     */
    public void pass() {
        this.state = GameTurnState.PASSED;
    }
}
