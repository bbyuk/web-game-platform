package com.bb.webcanvasservice.domain.game.repository;

import org.springframework.data.repository.query.Param;

/**
 * 게임 세션 커스텀 레포지토리
 */
public interface GameSessionCustomRepository {
    boolean isAllUserLoaded(@Param("gameSessionId") Long gameSessionId);
}
