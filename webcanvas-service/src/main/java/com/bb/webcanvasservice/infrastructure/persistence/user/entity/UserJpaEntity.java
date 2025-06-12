package com.bb.webcanvasservice.infrastructure.persistence.user.entity;

import com.bb.webcanvasservice.infrastructure.persistence.common.BaseEntity;
import com.bb.webcanvasservice.domain.user.model.UserStateCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * webcanvas 게임 내에서의 유저 정보 엔티티
 * 유저의 기준 정보는 플랫폼 서비스에서 관리하며, 게임 플레이에 필요한 유저 식별 정보만을 담당한다.
 */
@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "id")
    /**
     * 유저 id
     */
    private Long id;

    @Column(name = "fingerprint", updatable = false, nullable = false, unique = true)
    /**
     * 서버에서 유저 등록시 생성된 유저 fingerprint
     */
    private String fingerprint;

    @Column(name = "refresh_token")
    /**
     * 발급된 accessToken이 만료될 경우 refresh를 위한 토큰
     */
    private String refreshToken;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    /**
     * 유저 상태
     */
    private UserStateCode state;

    public UserJpaEntity(Long id, String fingerprint, String refreshToken, UserStateCode state) {
        this.id = id;
        this.fingerprint = fingerprint;
        this.refreshToken = refreshToken;
        this.state = state;
    }

    /**
     * 유저에게 할당된 refreshToken을 업데이트한다.
     * @param refreshToken
     */
    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    /**
     * 유저 상태를 변경한다.
     * @param state
     */
    public void changeState(UserStateCode state) {
        this.state = state;
    }


    /**
     * 현재 플레이중인 게임을 완료하고 게임 방으로 돌아간다.
     */
    public void endGameAndResetToRoom() {
        if (this.state == UserStateCode.IN_GAME) {
            this.state = UserStateCode.IN_ROOM;
        }
    }
}
