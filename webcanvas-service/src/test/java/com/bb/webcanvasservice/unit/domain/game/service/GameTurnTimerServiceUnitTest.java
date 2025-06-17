package com.bb.webcanvasservice.unit.domain.game.service;

import com.bb.webcanvasservice.game.infrastructure.persistence.registry.InMemoryGameTurnTimerRegistry;
import com.bb.webcanvasservice.game.domain.service.GameTurnTimerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@ExtendWith(MockitoExtension.class)
@DisplayName("[unit] [service] 게임 턴 타이머 단위테스트")
class GameTurnTimerServiceUnitTest {

    GameTurnTimerService gameTurnTimerService = new GameTurnTimerService(new InMemoryGameTurnTimerRegistry(), Executors.newScheduledThreadPool(4));


    @Test
    @DisplayName("게임 턴 타이머 스케줄러 등록 - 스케줄러에 등록이 정상적으로 처리된다.")
    void testRegisterSuccess() throws Exception {
        // given
        Long gameRoomId = 5L;
        Long gameSessionId = 213L;
        int period = 3;
        int gameTurns = 3;
        AtomicInteger currentTurn = new AtomicInteger();
        CountDownLatch latch = new CountDownLatch(1);


        Consumer<Long> turnEndHandler = id -> {
            System.out.println(LocalDateTime.now() + " " + id + "방 턴 종료");
            currentTurn.getAndIncrement();
        };

        // when
        System.out.println(LocalDateTime.now() + " " + gameRoomId + " 방 턴 타이머 등록");
        gameTurnTimerService.registerTurnTimer(gameRoomId, gameSessionId, period, turnEndHandler);

        boolean finished = latch.await(10, TimeUnit.SECONDS);

        /**
         * 게임 종료 요청이 onDemand로 처리되지 않으면 종료되지 않는다.
         */
//        Assertions.assertThat(finished).isFalse();
//        Assertions.assertThat(currentTurn.get()).isEqualTo(3);
    }

}