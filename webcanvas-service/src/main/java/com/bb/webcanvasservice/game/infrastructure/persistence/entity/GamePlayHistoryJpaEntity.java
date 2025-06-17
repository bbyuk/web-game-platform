package com.bb.webcanvasservice.game.infrastructure.persistence.entity;

import com.bb.webcanvasservice.common.infrastructure.persistence.BaseEntity;
import com.bb.webcanvasservice.user.infrastructure.persistence.entity.UserJpaEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 유저별 게임 플레이 이력 entity
 */
@Getter
@Entity
@Table(name = "game_play_histories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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
    @JoinColumn(name = "user_id")
    private UserJpaEntity userEntity;

    /**
     * 게임을 플레이한 세션
     */
    @JoinColumn(name = "game_session_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private GameSessionJpaEntity gameSessionEntity;
}
