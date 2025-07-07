package com.bb.webcanvasservice.chat.domain.port.game;

/**
 * chat -> game 포트
 */
public interface ChatGamePort {

    /**
     * 정답 여부를 확인하고, 정답 시 정답 이벤트를 발행한다.
     * @param value 입력 값
     */
    void checkAnswer(Long gameRoomId, Long senderId, String value);

    /**
     * sender가 drawer인지 여부를 확인한다.
     *
     * @param gameSessionId 대상 게임 세션 ID
     * @param senderId 메세지 전송자 유저 ID
     * @return drawer 여부
     */
    boolean isDrawer(Long gameSessionId, Long senderId);
}
