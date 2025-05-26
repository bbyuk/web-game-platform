package com.bb.webcanvasservice.domain.game.entity;

import com.bb.webcanvasservice.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 유저별 게임 플레이 이력 entity
 */
@Getter
@Entity
@Table(name = "game_play_histories")
@RequiredArgsConstructor
public class GamePlayHistory {

    @Id @GeneratedValue
    @Column(name = "id")
    /**
     * 게임 플레이 이력 ID
     */
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    /**
     * 게임을 플레이한 유저
     */
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_session_id")
    /**
     * 게임을 플레이한 세션
     */
    private GameSession gameSession;

    public GamePlayHistory(User user, GameSession gameSession) {
        this.user = user;
        this.gameSession = gameSession;
    }


}
