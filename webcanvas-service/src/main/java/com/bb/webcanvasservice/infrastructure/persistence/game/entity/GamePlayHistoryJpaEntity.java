package com.bb.webcanvasservice.infrastructure.persistence.game.entity;

import com.bb.webcanvasservice.common.entity.BaseEntity;
import com.bb.webcanvasservice.infrastructure.persistence.user.entity.UserJpaEntity;
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
public class GamePlayHistoryJpaEntity extends BaseEntity {

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
    private UserJpaEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_session_id")
    /**
     * 게임을 플레이한 세션
     */
    private GameSessionJpaEntity gameSession;

    public GamePlayHistoryJpaEntity(UserJpaEntity user, GameSessionJpaEntity gameSession) {
        this.user = user;
        this.gameSession = gameSession;
    }


}
