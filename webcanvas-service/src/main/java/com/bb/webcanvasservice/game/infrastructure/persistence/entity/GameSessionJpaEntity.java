package com.bb.webcanvasservice.game.infrastructure.persistence.entity;

import com.bb.webcanvasservice.game.domain.model.GameSessionState;
import com.bb.webcanvasservice.common.infrastructure.persistence.BaseEntity;
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


    /**
     * 게임 세션을 종료하고 게임 세션과 연관되어 있는 게임방 객체의 상태 리셋을 요청한다.
     */
    public void end() {
        state = GameSessionState.COMPLETED;
    }

    /**
     * 게임 세션을 시작한다.
     */
    public void start() {
        state = GameSessionState.PLAYING;
    }

    /**
     * 게임 세션이 현재 플레이 중인지 확인한다.
     * @return 게임 세션 현재 플레이 여부
     */
    public boolean isPlaying() {
        return state == GameSessionState.PLAYING;
    }

    /**
     * 게임 세션이 활성상태인지 확인한다.
     * @return 게임 세션 현재 활성 여부
     */
    public boolean isActive() {
        return state == GameSessionState.PLAYING || state == GameSessionState.LOADING;
    }

    /**
     * 게임 종료인지 여부를 체크한다.
     * @return 게임 종료 여부
     */
    public boolean isEnd() {
        return state == GameSessionState.COMPLETED;
    }
}
