package com.bb.webcanvasservice.common.lock.async;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface AsyncLockCallback<T> {
    CompletableFuture<T> doInLock();
}
