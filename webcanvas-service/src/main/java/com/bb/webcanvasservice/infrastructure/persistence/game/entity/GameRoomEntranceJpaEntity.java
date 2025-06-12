package com.bb.webcanvasservice.infrastructure.persistence.game.entity;

import com.bb.webcanvasservice.domain.game.model.GameRoomEntranceRole;
import com.bb.webcanvasservice.domain.game.model.GameRoomEntranceState;
import com.bb.webcanvasservice.infrastructure.persistence.common.BaseEntity;
import com.bb.webcanvasservice.infrastructure.persistence.user.entity.UserJpaEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.bb.webcanvasservice.domain.game.model.GameRoomEntranceRole.HOST;

/**
 * 유저의 게임 방 입장을 나타내는 엔티티 클래스
 */
@Entity
@Getter
@Table(name = "game_room_entrances")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameRoomEntranceJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "id")
    /**
     * 게임 방 입장 ID
     */
    private Long id;

    /**
     * 게임 방
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_room_id")
    private GameRoomJpaEntity gameRoomEntity;

    /**
     * 입장한 유저
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserJpaEntity userEntity;

    @Column(name = "user_nickname")
    /**
     * 게임 방 내에서의 유저 닉네임 -> 사전에서 랜덤으로 찾아와 조합할 예정
     */
    private String nickname;

    @Column(name = "room_role")
    @Enumerated(EnumType.STRING)
    /**
     * 게임 방 내에서의 역할
     */
    private GameRoomEntranceRole role;

    /**
     * 게임 방 입장 기록 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private GameRoomEntranceState state;

    @Column(name = "ready")
    private boolean ready;

    public boolean isReady() {
        if (this.role == HOST) {
            return true;
        }

        return ready;
    }

    public GameRoomEntranceJpaEntity(Long id, GameRoomJpaEntity gameRoomEntity, UserJpaEntity userEntity, GameRoomEntranceState state, String nickname, GameRoomEntranceRole role) {
        this.id = id;
        this.gameRoomEntity = gameRoomEntity;
        this.userEntity = userEntity;
        this.state = state;
        this.nickname = nickname;
        this.role = role;
        this.ready = role == HOST;
    }

    /**
     * 게임 입장 Entity를 exit 처리한다.
     */
    public void exit() {
        this.state = GameRoomEntranceState.EXITED;
    }

    /**
     * 역할을 변경한다.
     * @param gameRoomEntranceRole
     */
    public void changeRole(GameRoomEntranceRole gameRoomEntranceRole) {
        this.role = gameRoomEntranceRole;
        if (this.role == HOST) {
            this.ready = true;
        }
    }

    /**
     * 레디 상태를 바꾼다.
     */
    public void changeReady(boolean ready) {
        this.ready = ready;
    }

    /**
     * GameRoomEntranceState를 변경한다.
     * @param state
     */
    public void changeState(GameRoomEntranceState state) {
        this.state = state;
    }

    /**
     * 게임 방 입장 정보 entity의 상태를 초기화하고, 유저 entity의 상태 초기화를 요청한다.
     */
    public void resetGameRoomEntranceInfo() {
        this.state = GameRoomEntranceState.WAITING;
    }
}
