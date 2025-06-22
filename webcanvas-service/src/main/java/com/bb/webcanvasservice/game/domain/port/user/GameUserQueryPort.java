package com.bb.webcanvasservice.game.domain.port.user;

/**
 * game -> user 조회 포트
 */
public interface GameUserQueryPort {
    boolean userCanJoin(Long userId);
}
