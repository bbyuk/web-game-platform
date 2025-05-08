package com.bb.webcanvasservice.common.lock.async;

import java.util.concurrent.CompletableFuture;

/**
 * /**
 * 비동기 분산락 내에서 수행할 로직 콜백
 * @param <T>
 */
@FunctionalInterface
public interface AsyncLockCallback<T> {
    CompletableFuture<T> doInLock();
}
