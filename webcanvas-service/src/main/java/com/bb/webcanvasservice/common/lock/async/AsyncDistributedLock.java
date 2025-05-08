package com.bb.webcanvasservice.common.lock.async;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface AsyncDistributedLock {
    <T> CompletableFuture<T> executeWithLock(String lockKey, AsyncLockCallback<T> asyncCallBack);
}
