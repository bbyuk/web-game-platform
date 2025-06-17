package com.bb.webcanvasservice.game.infrastructure.persistence.entity;

import com.bb.webcanvasservice.game.domain.model.GameRoomState;
import com.bb.webcanvasservice.common.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게임 방을 나타내는 엔티티 클래스
 * 게임 방의 정보를 저장한다.
 */
@Entity
@Getter
@Table(name = "game_rooms")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GameRoomJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "id")
    /**
     * 게임 방 ID
     */
    private Long id;

    @Column(name = "join_code", length = 12)
    /**
     * 게임 방의 입장 코드
     */
    private String joinCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    /**
     * 게임 방 상태
     */
    private GameRoomState state;


    public void changeStateToPlay() {
        this.state = GameRoomState.PLAYING;
    }

    /**
     * 게임 세션이 종료된 후 WAITING 상태로 방 상태를 리셋한다.
     */
    public void resetGameRoomState() {
        this.state = GameRoomState.WAITING;
    }

    /**
     * 게임 방 상태를 close한다.
     */
    public void close() {
        this.state = GameRoomState.CLOSED;
    }

}
