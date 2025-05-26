package com.bb.webcanvasservice.domain.game.entity;

import com.bb.webcanvasservice.domain.game.enums.GameSessionState;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 게임의 세션을 나타내는 엔티티 클래스
 */
@Entity
@Getter
@Table(name = "game_sessions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameSession {

    @Id
    @GeneratedValue
    @Column(name = "id")
    /**
     * 게임 세션 ID
     */
    private Long id;

    @Column(name = "turn_count")
    /**
     * 게임 턴 수
     */
    private int turnCount;

    @Column(name = "time_per_turn")
    /**
     * 턴 당 시간
     */
    private int timePerTurn;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    /**
     * 게임 세션의 상태
     */
    private GameSessionState state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_room_id")
    /**
     * 게임 방
     */
    private GameRoom gameRoom;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "gameSession")
    /**
     * 세션에 포함되어 있는 턴 목록
     */
    private List<GameTurn> gameTurns = new ArrayList<>();

    public GameSession(GameRoom gameRoom, int turnCount, int timePerTurn) {
        this.gameRoom = gameRoom;
        this.state = GameSessionState.PLAYING;
        this.turnCount = turnCount;
        this.timePerTurn = timePerTurn;
    }

}
