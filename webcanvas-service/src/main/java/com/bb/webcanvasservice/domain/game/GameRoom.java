package com.bb.webcanvasservice.domain.game;

import jakarta.persistence.*;

/**
 * 게임 방을 나타내는 엔티티 클래스
 * 게임 방의 정보를 저장한다.
 */
@Entity
@Table(name = "game_room")
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


}
