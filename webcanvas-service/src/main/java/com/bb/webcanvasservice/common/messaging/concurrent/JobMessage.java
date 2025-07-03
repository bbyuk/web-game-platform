package com.bb.webcanvasservice.common.messaging.concurrent;

/**
 * 메세지 큐에 담을 job message
 */
public record JobMessage(
        String type,
        String payload
) {
}
