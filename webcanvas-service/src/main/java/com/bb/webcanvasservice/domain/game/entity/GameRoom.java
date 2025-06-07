package com.bb.webcanvasservice.domain.game.entity;

import com.bb.webcanvasservice.common.entity.BaseEntity;
import com.bb.webcanvasservice.domain.game.enums.GameRoomEntranceState;
import com.bb.webcanvasservice.domain.game.enums.GameRoomState;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 게임 방을 나타내는 엔티티 클래스
 * 게임 방의 정보를 저장한다.
 */
@Entity
@Getter
@Table(name = "game_rooms")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameRoom extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "id")
    /**
     * 게임 방 ID
     */
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    /**
     * 게임 방 상태
     */
    private GameRoomState state;

    @Column(name = "join_code", length = 12)
    /**
     * 게임 방의 입장 코드
     */
    private String joinCode;

    @OneToMany(mappedBy = "gameRoom", fetch = FetchType.EAGER)
    /**
     * 게임 방의 입장 목록
     */
    private List<GameRoomEntrance> entrances = new ArrayList<>();

    public GameRoom(GameRoomState state, String joinCode) {
        this.state = state;
        this.joinCode = joinCode;
    }

    public GameRoom(String joinCode) {
        this(GameRoomState.WAITING, joinCode);
    }

    public void addEntrance(GameRoomEntrance... entrance) {
        entrances.addAll(Arrays.stream(entrance).toList());
    }

    public void changeStateToPlay() {
        this.state = GameRoomState.PLAYING;
    }

    /**
     * 게임 세션이 종료된 후 WAITING 상태로 방 상태를 리셋한다.
     */
    public void resetGameRoomState() {
        this.state = GameRoomState.WAITING;
    }

    public void close() {
        this.state = GameRoomState.CLOSED;
    }

    public int getEnteredUserCount() {
        return (int) entrances
                .stream()
                .filter(entrance
                        -> GameRoomEntranceState.entered.contains(entrance.getState())).count();
    }

}
