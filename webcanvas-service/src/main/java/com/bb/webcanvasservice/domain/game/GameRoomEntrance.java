package com.bb.webcanvasservice.domain.game;

import com.bb.webcanvasservice.domain.game.enums.GameRoomEntranceState;
import com.bb.webcanvasservice.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    /**
     * 게임 방 입장 기록 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "game_room_entrance_state")
    private GameRoomEntranceState state;

    public GameRoomEntrance(GameRoom gameRoom, User user, String nickname) {
        this.gameRoom = gameRoom;
        this.user = user;
        this.state = GameRoomEntranceState.ACTIVE;
        this.nickname = nickname;
    }

    public void exit() {
        this.state = GameRoomEntranceState.INACTIVE;
    }
}
