package com.bb.webcanvasservice.domain.game;

import jakarta.persistence.*;
import lombok.Getter;

/**
 * 게임 방을 나타내는 엔티티 클래스
 * 게임 방의 정보를 저장한다.
 */
@Entity
@Getter
@Table(name = "game_rooms")
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


}
