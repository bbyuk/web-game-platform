package com.bb.webcanvasservice.security.websocket;

import com.bb.webcanvasservice.domain.canvas.CanvasService;
import com.bb.webcanvasservice.domain.game.GameRoomService;
import com.bb.webcanvasservice.security.auth.WebCanvasAuthentication;
import com.bb.webcanvasservice.security.exception.BadAccessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

/**
 * 게임 방 이벤트 브로커 구독 등 웹 소켓 이벤트 구독 요청 시 적절한 요청인지를 확인하는 ChannelInterceptor
 */
@Slf4j
@RequiredArgsConstructor
public class SubscribeChannelInterceptor implements ChannelInterceptor {

    private final GameRoomService gameRoomService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        var accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            WebCanvasAuthentication authentication = (WebCanvasAuthentication) SecurityContextHolder.getContext().getAuthentication();
            String destination = accessor.getDestination();

            if (StringUtils.hasText(destination) && destination.startsWith(CanvasService.CANVAS_DESTINATION_PREFIX)) {
                /**
                 * 입장한 게임 방의 Canvas websocket 서버에 구독 요청 시 구독 요청이 유효한지 검증
                 */
                Long userId = authentication.getUserId();
                Long gameRoomId = extractGameRoomId(destination);

                if (!gameRoomService.canEnterWebSocketGameRoom(gameRoomId, userId)) {
                    log.debug("현재 게임 방에 입장된 정보가 없음.");
                    log.debug("gameRoomId : {}", gameRoomId);
                    log.debug("userId : {}", userId);

                    throw new BadAccessException("잘못된 접근입니다.");
                }
            }
        }

        return ChannelInterceptor.super.preSend(message, channel);
    }

    private Long extractGameRoomId(String destination) {


        String[] parts = destination.split("/");
        return Long.parseLong(parts[2]);
    }
}
