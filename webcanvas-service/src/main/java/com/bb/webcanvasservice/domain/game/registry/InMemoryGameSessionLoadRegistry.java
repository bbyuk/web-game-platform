package com.bb.webcanvasservice.domain.game.registry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 인메모리 게임 세션 로드 등록 처리
 * 동시성 문제 해결을 위한 컴포넌트로 ConcurrentHashMap을 이용한다.
 */
@Slf4j
@Component
public class InMemoryGameSessionLoadRegistry implements GameSessionLoadRegistry {

    private final Map<Long, Set<Long>> registry = new ConcurrentHashMap<>();
    private final Map<Long, Object> lockMap = new ConcurrentHashMap<>();


    @Override
    public void register(Long gameSessionId, Long userId) {
        log.debug("register gameSessionId={} userId={}", gameSessionId, userId);

        Object lock = this.lockMap.computeIfAbsent(gameSessionId, k -> new Object());
        synchronized (lock) {
            // 세션별 유저 Set을 생성/추가
            registry.computeIfAbsent(gameSessionId, k -> ConcurrentHashMap.newKeySet())
                    .add(userId);
        }
    }

    @Override
    public void clear(Long gameSessionId) {
        log.debug("clear gameSessionId={}", gameSessionId);
        Object lock = this.lockMap.get(gameSessionId);

        if (lock != null) {
            synchronized (lock) {
                registry.remove(gameSessionId);
                this.lockMap.remove(gameSessionId);
            }
        }
    }

    @Override
    public boolean isAllLoaded(Long gameSessionId, int enteredUserCount) {
        Object lock = this.lockMap.get(gameSessionId);
        if (lock == null) return false;

        synchronized (lock) {
            Set<Long> users = registry.get(gameSessionId);
            log.debug("registry size={} enteredUserCount={}", users.size(), enteredUserCount);

            return users.size() == enteredUserCount;
        }
    }
}
