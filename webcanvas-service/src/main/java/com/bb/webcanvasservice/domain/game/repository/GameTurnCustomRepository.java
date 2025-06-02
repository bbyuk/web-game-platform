package com.bb.webcanvasservice.domain.game.repository;

import com.bb.webcanvasservice.domain.game.entity.GameTurn;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GameTurnCustomRepository {
    /**
     * 가장 최신의(마지막) 턴을 조회한다.
     * @param gameSessionId
     * @return
     */
    Optional<GameTurn> findLastTurn(Long gameSessionId);
}
