package com.bb.webcanvasservice.infrastructure.messaging.concurrent;
import com.bb.webcanvasservice.common.messaging.concurrent.*;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


@Component
public class InMemoryMessageQueue implements MessageQueue {

    private final BlockingQueue<JobMessage> queue;

    private final int capacity = 1000;

    public InMemoryMessageQueue() {
        this.queue = new LinkedBlockingQueue<>(capacity);
    }

    @Override
    public void enqueue(JobMessage message) {
        queue.add(message);
    }

    @Override
    public JobMessage dequeue() {
        return queue.remove();
    }

    @Override
    public int size() {
        return queue.size();
    }
}
