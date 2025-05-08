package com.bb.webcanvasservice.common.lock;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 인메모리 분산 락 구현체
 */
@Component
public class InMemoryDistributedLock implements DistributedLock {
    private final ConcurrentHashMap<String, AtomicBoolean> lockMap = new ConcurrentHashMap<>();

    @Override
    public <T> T executeWithLock(String lockKey, LockCallback<T> callback) {
        AtomicBoolean lock = lockMap.computeIfAbsent(lockKey, key -> new AtomicBoolean(false));

        if (!lock.compareAndSet(false, true)) {
            throw new LockAlreadyOccupiedException();
        }

        try {
            return callback.doInLock();
        }
        finally {
            lock.set(false);
        }
    }
}
