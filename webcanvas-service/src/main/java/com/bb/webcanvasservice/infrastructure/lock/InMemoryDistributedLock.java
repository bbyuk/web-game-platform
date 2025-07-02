package com.bb.webcanvasservice.infrastructure.lock;

import com.bb.webcanvasservice.common.lock.DistributedLock;
import com.bb.webcanvasservice.common.lock.callback.AsyncLockCallback;
import com.bb.webcanvasservice.common.lock.callback.LockCallback;
import com.bb.webcanvasservice.infrastructure.lock.exception.LockAlreadyOccupiedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 인메모리 분산 락 구현체
 */
@Slf4j
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
    
    @Override
    public <T> CompletableFuture<T> executeAsyncWithLock(String lockKey, AsyncLockCallback<T> asyncCallBack) {
        AtomicBoolean lock = lockMap.computeIfAbsent(lockKey, key -> new AtomicBoolean(false));

        if (!lock.compareAndSet(false, true)) {
//            CompletableFuture<T> failureFuture = new CompletableFuture<>();
            throw new LockAlreadyOccupiedException();
//            failureFuture.completeExceptionally(new LockAlreadyOccupiedException());
//            return failureFuture;
        }

        try {
            return asyncCallBack.doInLock()
                    .whenComplete((result, throwable) -> {
                        lock.set(false);
                    });
        }
        catch (Exception e) {
            lock.set(false);
            log.error(e.getMessage(), e);
            CompletableFuture<T> failureFuture = new CompletableFuture<>();
            failureFuture.completeExceptionally(e);
            return failureFuture;
        }
    }
}
