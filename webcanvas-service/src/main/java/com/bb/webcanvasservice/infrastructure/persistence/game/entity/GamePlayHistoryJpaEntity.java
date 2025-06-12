package com.bb.webcanvasservice.infrastructure.persistence.game.entity;

import com.bb.webcanvasservice.domain.user.model.User;
import com.bb.webcanvasservice.infrastructure.persistence.common.BaseEntity;
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

    /**
     * 게임을 플레이한 유저
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserJpaEntity userEntity;

    /**
     * 게임을 플레이한 세션
     */
    @JoinColumn(name = "game_session_id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private GameSessionJpaEntity gameSessionEntity;

    public GamePlayHistoryJpaEntity(UserJpaEntity userEntity, GameSessionJpaEntity gameSessionEntity) {
        this.userEntity = userEntity;
        this.gameSessionEntity = gameSessionEntity;
    }


}
