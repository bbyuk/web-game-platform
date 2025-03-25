package com.bb.webcanvasservice.domain.game;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 게임 방을 나타내는 엔티티 클래스
 * 게임 방의 정보를 저장한다.
 */
@Entity
@Getter
@Table(name = "game_rooms")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameRoom {

    @Id
    @GeneratedValue
    @Column(name = "id")
    /**
     * 게임 방 ID
     */
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    /**
     * 게임 방 상태
     */
    private GameRoomStatus status;

    @Column(name = "join_code", length = 12)
    /**
     * 게임 방의 입장 코드
     */
    private String joinCode;

    @OneToMany(mappedBy = "gameRoom")
    /**
     * 게임 방의 입장 목록
     */
    private List<GameRoomEntrance> entrances = new ArrayList<>();

    public GameRoom(GameRoomStatus status, String joinCode) {
        this.status = status;
        this.joinCode = joinCode;
    }

}
