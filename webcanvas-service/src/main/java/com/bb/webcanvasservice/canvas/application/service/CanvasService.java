package com.bb.webcanvasservice.canvas.application.service;

import com.bb.webcanvasservice.canvas.application.command.StrokeCommand;
import com.bb.webcanvasservice.common.messaging.websocket.MessageSender;
import com.bb.webcanvasservice.canvas.domain.model.Stroke;
import com.bb.webcanvasservice.infrastructure.messaging.websocket.config.WebSocketProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 캔버스에 그려진 Stroke 획을 받아 브로드캐스팅 처리를 담당하는 서비스 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CanvasService {
    private final MessageSender messageSender;
    private final WebSocketProperties webSocketProperties;

    /**
     * 웹 소켓 컨트롤러를 통해 들어온 Stroke 요청을 같은 방에 있는 유저들에게 브로드캐스팅한다.
     *
     * @param command 스트로크 커맨드
     */
    @Transactional(readOnly = true)
    public void broadcastStrokeOnRoom(StrokeCommand command) {
        log.debug("message sender ====== {}", command.userId());

        /**
         * TODO gameSession 및 턴 validation 체크 필요
         */

        /**
         * gameRoom id에 해당하는 토픽으로 브로드캐스팅
         * /session/{gameRoomId}/canvas 브로커를 구독중인 클라이언트로 stroke 이벤트 브로드캐스팅
         */
        String targetBroker = String.format("%s/%d/%s",
                webSocketProperties.topic().main().gameRoom(),
                command.gameSessionId(),
                webSocketProperties.topic().sub().canvas());

        log.info("send to broker => {}", targetBroker);

        Stroke newStroke = Stroke.createNewStroke(command.color(), command.lineWidth(), command.points());

        /**
         * TODO newStroke를 비동기적으로 세션에 포함시켜 저장한다.
         */

        messageSender.send(targetBroker, newStroke);
    }
}
