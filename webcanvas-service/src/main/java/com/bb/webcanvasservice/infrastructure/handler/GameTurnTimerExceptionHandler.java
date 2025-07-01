package com.bb.webcanvasservice.infrastructure.handler;

import com.bb.webcanvasservice.common.handler.AsyncExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GameTurnTimerExceptionHandler implements AsyncExceptionHandler {
    @Override
    public void handle(Exception e) {
        log.error(e.getMessage(), e);
    }
}
