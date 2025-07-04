package com.bb.webcanvasservice.game.infrastructure.persistence.entity;

import com.bb.webcanvasservice.game.domain.model.session.GameSessionState;
import com.bb.webcanvasservice.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게임의 세션을 나타내는 엔티티 클래스
 */
@Entity
@Getter
@Table(name = "game_sessions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GameSessionJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "id")
    /**
     * 게임 세션 ID
     */
    private Long id;

    /**
     * 게임 방 JpaEntity
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_room_id")
    private GameRoomJpaEntity gameRoomEntity;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    /**
     * 게임 세션의 상태
     */
    private GameSessionState state;

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
}
