package com.bb.webcanvasservice.infrastructure.concurrent;

import com.bb.webcanvasservice.common.cuncurrent.SerializedTaskExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

@Slf4j
@Component
public class InMemorySerializedTaskExecutor implements SerializedTaskExecutor {

    private final Map<String, TaskQueue> taskQueues = new ConcurrentHashMap<>();
    private final ExecutorService taskExecutor = Executors.newCachedThreadPool();

    @Override
    public void execute(String key, Runnable task) {
        TaskQueue taskQueue = taskQueues.computeIfAbsent(key, k -> new TaskQueue());
        taskQueue.enqueue(() -> {
            try {
                task.run();
                return CompletableFuture.completedFuture(null);
            }
            catch(Exception e) {
                log.error("Runnable task execution failed : {}", e.getMessage(), e);
                return CompletableFuture.failedFuture(e);
            }
        });
    }

    @Override
    public <T> CompletableFuture<T> execute(String key, Supplier<CompletableFuture<T>> supplier) {
        TaskQueue taskQueue = taskQueues.computeIfAbsent(key, k -> new TaskQueue());
        CompletableFuture<T> resultFuture = new CompletableFuture<>();

        taskQueue.enqueue(() -> {
            try {
                return supplier.get().whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        resultFuture.completeExceptionally(throwable);
                    }
                    else {
                        resultFuture.complete(result);
                    }
                });
            }
            catch(Exception e) {
                resultFuture.completeExceptionally(e);
                return CompletableFuture.failedFuture(e);
            }
        });

        return resultFuture;
    }


    /**
     * 작업 직렬 실행 보장 큐
     */
    private class TaskQueue {
        private final Queue<Supplier<CompletableFuture<?>>> queue = new ConcurrentLinkedQueue<>();
        private final AtomicBoolean isRunning = new AtomicBoolean(false);

        public void enqueue(Supplier<CompletableFuture<?>> taskSupplier) {
            queue.add(taskSupplier);
            tryStart();
        }

        private void tryStart() {
            if (isRunning.compareAndSet(false, true)) {
                taskExecutor.submit(this::processQueue);
            }
        }

        private void processQueue() {
            while(true) {
                Supplier<CompletableFuture<?>> taskSupplier = queue.poll();
                if (taskSupplier == null) {
                    isRunning.set(false);
                    if (queue.isEmpty() && isRunning.compareAndSet(false, true)) continue;
                    return;
                }

                try {
                    taskSupplier.get().join();
                }
                catch(Exception e) {
                    log.error("Serialized task execution error : {}", e.getMessage(), e);
                }
            }
        }
    }
}
