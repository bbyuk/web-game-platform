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

    @Column(name = "user_id")
    /**
     * 게임을 플레이한 유저 ID
     */
    private Long userId;

    @Column(name = "game_session_id")
    /**
     * 게임을 플레이한 세션 ID
     */
    private Long gameSessionId;

    public GamePlayHistoryJpaEntity(Long userId, Long gameSessionId) {
        this.userId = userId;
        this.gameSessionId = gameSessionId;
    }


}
