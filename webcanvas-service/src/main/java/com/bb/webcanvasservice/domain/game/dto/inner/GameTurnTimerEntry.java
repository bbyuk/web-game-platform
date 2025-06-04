package com.bb.webcanvasservice.domain.game.dto.inner;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;
import java.util.function.Function;

@Schema(description = "게임 턴 타이머로 등록할 엔트리")
public record GameTurnTimerEntry(
        @Schema(description = "실질적인 타이머 역할을 하는 future 객체")
        ScheduledFuture<?> future,

        @Schema(description = "타이머 간격")
        int period,

        @Schema(description = "턴 종료시 작업할 핸들러")
        Consumer<Long> turnEndHandler
) {
}
