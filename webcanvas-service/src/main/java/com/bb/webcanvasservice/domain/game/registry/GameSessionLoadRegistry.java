package com.bb.webcanvasservice.domain.game.registry;

/**
 * 게임 세션 구독 등록
 * 동시성 문제를 해결하기 위함
 *
 */
public interface GameSessionLoadRegistry {
    /**
     * 로드 된 시점에 등록한다.
     * @param gameSessionId
     * @param userId
     */
    void register(Long gameSessionId, Long userId);

    /**
     * 유저 로드 체크 중 게임이 시작되거나 중단된 경우
     * @param gameSessionId
     */
    void clear(Long gameSessionId);

    /**
     * 모든 유저가 로드가 완료되었는지 체크한다.
     * @param gameSessionId
     * @param enteredUserCount
     * @return
     */
    boolean isAllLoaded(Long gameSessionId, int enteredUserCount);
}
