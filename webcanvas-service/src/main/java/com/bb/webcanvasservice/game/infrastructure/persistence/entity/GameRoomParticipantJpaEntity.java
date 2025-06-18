package com.bb.webcanvasservice.game.infrastructure.persistence.entity;

import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoomParticipantRole;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoomParticipantState;
import com.bb.webcanvasservice.infrastructure.persistence.BaseEntity;
import com.bb.webcanvasservice.user.infrastructure.persistence.entity.UserJpaEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게임 방 입장자를 나타내는 엔티티 클래스
 */
@Entity
@Getter
@Table(name = "game_room_participants")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GameRoomParticipantJpaEntity extends BaseEntity {

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
     * 유저
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
    private GameRoomParticipantRole role;

    /**
     * 게임 방 입장 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private GameRoomParticipantState state;

    /**
     * 레디 상태
     */
    @Column(name = "ready")
    private boolean ready;
}
