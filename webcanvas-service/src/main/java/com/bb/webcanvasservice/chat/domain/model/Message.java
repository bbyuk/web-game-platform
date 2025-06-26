package com.bb.webcanvasservice.chat.domain.model;


import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 채팅 도메인 메세지 모델
 */
public class Message {

    /**
     * 채팅 메세지 값
     */
    private final String value;

    /**
     * 발신자 ID
     */
    private final Long senderId;

    /**
     * 타임스탬프
     */
    private final LocalDateTime timestamp;

    /**
     * 전송 목적지
     */
    private final String destination;


    public Message(String value, Long senderId, String destination) {
        this.value = value;
        this.senderId = senderId;
        this.timestamp = LocalDateTime.now();
        this.destination = destination;
    }

    public static Message create(String value, Long senderId, String destination) {
        return new Message(value, senderId, destination);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(value, message.value) &&
                Objects.equals(senderId, message.senderId) &&
                Objects.equals(timestamp, message.timestamp) &&
                Objects.equals(destination, message.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, senderId, timestamp, destination);
    }

    public String getValue() {
        return value;
    }

    public Long getSenderId() {
        return senderId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDestination() {
        return destination;
    }
}
