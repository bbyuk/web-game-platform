package com.bb.webcanvasservice.chat.domain.port.game;

/**
 * chat -> game 포트
 */
public interface ChatGameCommandPort {

    /**
     * 정답 여부를 확인하고, 정답 시 정답 이벤트를 발행한다.
     * @param value 입력 값
     */
    void checkAnswer(Long gameRoomId, Long senderId, String value);
}
