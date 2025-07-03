package com.bb.webcanvasservice.common.messaging.concurrent;

/**
 * 동시성 문제를 위한 메세지 큐
 */
public interface MessageQueue {

    /**
     * 실행가능한 job을 message 삼아 Message Queue에 넣는다.
     */
    void enqueue(JobMessage message);

    /**
     * job을 실행하기 위해 Message Queue에서 꺼낸다.
     * @return peek message
     */
    JobMessage dequeue();

    /**
     * Message Queue의 size 리턴
     * @return size
     */
    int size();
}
