package com.bb.webcanvasservice.game.application.repository;

import com.bb.webcanvasservice.game.domain.model.GamePlayHistory;

import java.util.List;

/**
 * 게임 플레이 이력 레포지토리
 */
public interface GamePlayHistoryRepository {

    /**
     * 게임 세션에 해당하는 게임 플레이 정보 목록을 조회한다.
     * @param gameSessionId 게임 세션 ID
     * @return 게임 이력 목록
     */
    List<GamePlayHistory> findByGameSessionId(Long gameSessionId);

    /**
     * 게임 이력을 모두 저장한다
     * @param gamePlayHistories
     */
    void saveAll(List<GamePlayHistory> gamePlayHistories);
}
