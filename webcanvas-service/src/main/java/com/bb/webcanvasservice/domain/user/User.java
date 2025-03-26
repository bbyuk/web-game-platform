package com.bb.webcanvasservice.domain.user;

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
public class User {

    @Id
    @GeneratedValue
    @Column(name = "id")
    /**
     * 유저 id
     */
    private Long id;

    @Column(name = "user_token")
    /**
     * 유저 식별 토큰
     */
    private String userToken;

    public User(String userToken) {
        this.userToken = userToken;
    }
}
