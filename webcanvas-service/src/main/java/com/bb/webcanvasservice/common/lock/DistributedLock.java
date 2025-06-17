package com.bb.webcanvasservice.common.lock;

import com.bb.webcanvasservice.common.lock.callback.AsyncLockCallback;
import com.bb.webcanvasservice.common.lock.callback.LockCallback;

import java.util.concurrent.CompletableFuture;

/**
 * 동시성 문제 해결을 위한 분산락 인터페이스
 * 개발 단계 및 프로젝트 오픈 초기 단계에서는 AtomicBoolean 방식의 싱글 인스턴스 락 적용
 * TODO 이후 확장 단계에 레디스 분산락 적용 고려 필요
 */
public interface DistributedLock {
    /**
     * 동기 처리 락
     * @param lockKey
     * @param callback
     * @return
     * @param <T>
     */
    <T> T executeWithLock(String lockKey, LockCallback<T> callback);

    /**
     * 비동기 처리 락
     * @param lockKey
     * @param asyncCallBack
     * @return
     * @param <T>
     */
    <T> CompletableFuture<T> executeAsyncWithLock(String lockKey, AsyncLockCallback<T> asyncCallBack);
}
