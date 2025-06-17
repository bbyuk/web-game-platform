//package com.bb.webcanvasservice.infrastructure.websocket.interceptor;
//
//import com.bb.webcanvasservice.common.security.WebCanvasAuthentication;
//
//import com.bb.webcanvasservice.game.application.service.GameRoomService;
//import com.bb.webcanvasservice.game.application.service.GameService;
//import com.bb.webcanvasservice.infrastructure.security.web.exception.BadAccessException;
//import com.bb.webcanvasservice.infrastructure.websocket.config.WebSocketProperties;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.simp.stomp.StompCommand;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.messaging.support.ChannelInterceptor;
//import org.springframework.messaging.support.MessageHeaderAccessor;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.util.StringUtils;
//
///**
// * 게임 방 이벤트 브로커 구독 등 웹 소켓 이벤트 구독 요청 시 적절한 요청인지를 확인하는 ChannelInterceptor
// * TODO - 이후에 SubscribeMapping으로 이동 고려 비즈니스 로직과 구독 보안 인터셉터 로직이 결합되어 버림
// */
//@Slf4j
//@RequiredArgsConstructor
//public class SubscribeChannelInterceptor implements ChannelInterceptor {
//
//    private final GameRoomService gameRoomService;
//    private final GameService gameService;
//
//    private final WebSocketProperties webSocketProperties;
//
//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        var accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//
//        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
//            WebCanvasAuthentication authentication = (WebCanvasAuthentication) SecurityContextHolder.getContext().getAuthentication();
//            String destination = accessor.getDestination();
//            String ipAddress = accessor.getSessionAttributes() != null
//                    ? (String) accessor.getSessionAttributes().get("IP_ADDRESS") : "";
//
//            log.info("WebSocket CONNECT from IP: {}", ipAddress);
//
//            /**
//             * 게임 방 구독 처리
//             */
//            if (!StringUtils.hasText(destination)) {
//                return ChannelInterceptor.super.preSend(message, channel);
//            }
//
//            Long userId = authentication.getUserId();
//
//            if (destination.startsWith(webSocketProperties.topic().main().gameRoom())) {
//                /**
//                 * 입장한 게임 방의 Canvas websocket 서버에 구독 요청 시 구독 요청이 유효한지 검증
//                 */
//                Long gameRoomId = extractId(destination);
//
//                if (!gameRoomService.isEnteredRoom(gameRoomId, userId)) {
//                    log.debug("현재 게임 방에 입장된 정보가 없음.");
//                    log.debug("gameRoomId : {}", gameRoomId);
//                    log.debug("userId : {}", userId);
//
//                    throw new BadAccessException("잘못된 접근입니다.");
//                }
//            }
//
//            if (destination.startsWith(webSocketProperties.topic().main().gameSession())) {
//                Long gameSessionId = extractId(destination);
//
//                if (!gameService.inGameSession(gameSessionId, userId)) {
//                    log.debug("현재 게임 세션에 참여한 정보가 없음.");
//                    log.debug("gameSessionId : {}", gameSessionId);
//                    log.debug("userId : {}", userId);
//
//                    throw new BadAccessException("잘못된 접근입니다.");
//                }
//            }
//        }
//
//        return ChannelInterceptor.super.preSend(message, channel);
//    }
//
//    /**
//     * /session/{gameRoomId}/**
//     * 인 게임 방 관련 토픽들로부터 gameRoomId를 추출한다.
//     *
//     * @param destination
//     * @return
//     */
//    private Long extractId(String destination) {
//        String[] parts = destination.split("/");
//        return Long.parseLong(parts[2]);
//    }
//}
