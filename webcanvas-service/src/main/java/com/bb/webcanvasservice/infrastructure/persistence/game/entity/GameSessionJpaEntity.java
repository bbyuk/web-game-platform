package com.bb.webcanvasservice.infrastructure.persistence.game.entity;

import com.bb.webcanvasservice.infrastructure.persistence.common.BaseEntity;
import com.bb.webcanvasservice.domain.game.model.GameSessionState;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게임의 세션을 나타내는 엔티티 클래스
 */
@Entity
@Getter
@Table(name = "game_sessions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameSessionJpaEntity extends BaseEntity {

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

    @Column(name = "game_room_id")
    /**
     * 게임 방 ID
     */
    private Long gameRoomId;

    public GameSessionJpaEntity(Long gameRoomId, int turnCount, int timePerTurn) {
        this.gameRoomId = gameRoomId;
        this.state = GameSessionState.LOADING;
        this.turnCount = turnCount;
        this.timePerTurn = timePerTurn;
    }

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
