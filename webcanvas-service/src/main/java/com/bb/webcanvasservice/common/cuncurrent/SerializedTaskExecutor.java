package com.bb.webcanvasservice.common.cuncurrent;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * 동시성 문제 해결을 위한 순차 작업 Executor
 */
public interface SerializedTaskExecutor {

    /**
     * 주어진 key에 대해 순차적으로 작업을 실행한다.
     * 이전 작업이 끝날 때까지 대기하며, FIFO로 순차 처리를 보장한다.
     * @param key  작업 키
     * @param task 실행할 작업
     */
    void execute(String key, Runnable task);

    /**
     * 주어진 key에 대해 순차적으로 작업을 실행한다.
     * 이전 작업이 끝날 때까지 대기하며, FIFO로 순차 처리를 보장한다.
     *
     * 처리 결과를 리턴받기
     *
     * @param key       작업 키
     * @param supplier  실행할 작업
     * @return          작업 결과
     * @param <T>       리턴 타입
     */
    <T>CompletableFuture<T> execute(String key, Supplier<CompletableFuture<T>> supplier);

}
