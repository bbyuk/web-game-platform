package com.bb.webcanvasservice.common.lock.async;

import java.util.concurrent.CompletableFuture;

/**
 * 동시성 문제 해결을 위한 비동기 방식 분산 락 인터페이스
 * 개발 단계 및 프로젝트 오픈 초기 단계에서는 AtomicBoolean 방식의 싱글 인스턴스 락 적용
 * TODO 이후 확장 단계에 레디스 분산락 적용 고려 필요
 */
@FunctionalInterface
public interface AsyncDistributedLock {
    <T> CompletableFuture<T> executeWithLock(String lockKey, AsyncLockCallback<T> asyncCallBack);
}
