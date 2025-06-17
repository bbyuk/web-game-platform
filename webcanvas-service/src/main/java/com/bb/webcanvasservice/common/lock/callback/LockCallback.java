package com.bb.webcanvasservice.common.lock.callback;

/**
 * 동기 분산락 내에서 수행할 로직 콜백
 * @param <T>
 */
@FunctionalInterface
public interface LockCallback<T> {
    T doInLock();
}
