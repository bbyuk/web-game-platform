package com.bb.webcanvasservice.domain.game.entity;

import com.bb.webcanvasservice.domain.game.enums.GameRoomEntranceState;
import com.bb.webcanvasservice.domain.game.enums.GameRoomRole;
import com.bb.webcanvasservice.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.bb.webcanvasservice.domain.game.enums.GameRoomRole.HOST;

/**
 * 유저의 게임 방 입장을 나타내는 엔티티 클래스
 */
@Entity
@Getter
@Table(name = "game_room_entrances")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameRoomEntrance {

    @Id
    @GeneratedValue
    @Column(name = "id")
    /**
     * 게임 방 입장 ID
     */
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_room_id")
    /**
     * 입장한 게임 방
     */
    private GameRoom gameRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    /**
     * 입장한 유저
     */
    private User user;

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
    private GameRoomRole role;

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

    public GameRoomEntrance(GameRoom gameRoom, User user, String nickname, GameRoomRole role) {
        this.gameRoom = gameRoom;
        this.user = user;
        this.state = GameRoomEntranceState.WAITING;
        this.nickname = nickname;
        this.role = role;
        this.ready = role == HOST;
    }

    /**
     * 게임 입장 Entity를 exit 처리한다.
     */
    public void exit() {
        this.state = GameRoomEntranceState.EXITED;
        this.gameRoom.getEntrances()
                .removeIf(entrance -> entrance.id.equals(this.id));
    }

    /**
     * 역할을 변경한다.
     * @param gameRoomRole
     */
    public void changeRole(GameRoomRole gameRoomRole) {
        this.role = gameRoomRole;
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
}
