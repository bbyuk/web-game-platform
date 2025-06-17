package com.bb.webcanvasservice.infrastructure.websocket.registry;

/**
 * 웹소켓 연결 세션 저장소
 *
 * id : sessionId로 저장해 id별로 단일 연결만 지원하도록 보장
 */
public interface WebSocketSessionRegistry {

    /**
     * 연결시 userId : sessionId로 등록
     * @param userId
     * @param sessionId
     */
    void register(Long userId, String sessionId);

    /**
     * 연결 해제시 userId 요소 삭제
     * @param userId
     */
    void unregister(Long userId);

    /**
     * 유저가 세션을 보유중인지 체크
     * @param userId
     * @return
     */
    boolean hasSession(Long userId);

    /**
     * 모든 유저 세션을 clear한다.
     * TODO - 관리자 권한 처리 필요
     */
    void clear();
}
