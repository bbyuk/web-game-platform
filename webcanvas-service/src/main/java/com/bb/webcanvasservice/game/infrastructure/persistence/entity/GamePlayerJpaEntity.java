package com.bb.webcanvasservice.game.infrastructure.persistence.entity;

import com.bb.webcanvasservice.game.domain.model.session.GamePlayerState;
import com.bb.webcanvasservice.user.infrastructure.persistence.entity.UserJpaEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "game_players")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GamePlayerJpaEntity {

    @Id
    @GeneratedValue
    @Column(name = "id")
    /**
     * 게임 플레이어 ID
     */
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_session_id")
    /**
     * 플레이어가 로드된 게임 세션
     */
    private GameSessionJpaEntity gameSessionEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    /**
     * 플레이어의 유저 ID
     */
    private UserJpaEntity userEntity;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    /**
     * 플레이어 상태
     */
    private GamePlayerState state;

    @Column(name = "nickname")
    /**
     * 닉네임
     */
    private String nickname;

}
