package com.bb.webcanvasservice.domain.canvas;

import com.bb.webcanvasservice.domain.canvas.dto.Point;
import com.bb.webcanvasservice.domain.canvas.dto.Stroke;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(CanvasController.class)
class CanvasControllerMvcTest {

    @Autowired
    private MockMvc mockMvc; // 모의 환경에서 웹 계층을 테스트하기 위한 유틸리티 클래스

    @Test
    @DisplayName("클라이언트에서 발생한 Stroke 이벤트를 같은 방에 접속해 있는 유저들에게 브로드캐스팅한다.")
    void broadcastingWhenStrokeEventOccur() throws Exception {
        Stroke stroke = Stroke.builder()
                .gameId(2L)
                .userId(23L)
                .color("black")
                .lineWidth(5)
                .points(List.of(
                        Point.of(512, 284),
                        Point.of(512, 284),
                        Point.of(511, 289),
                        Point.of(506, 296),
                        Point.of(493, 314),
                        Point.of(463, 352),
                        Point.of(451, 370),
                        Point.of(428, 405),
                        Point.of(408, 434),
                        Point.of(404, 441),
                        Point.of(391, 462),
                        Point.of(384, 476),
                        Point.of(382, 481),
                        Point.of(379, 487),
                        Point.of(379, 489),
                        Point.of(379, 492),
                        Point.of(380, 493),
                        Point.of(384, 494),
                        Point.of(409, 494),
                        Point.of(449, 485),
                        Point.of(485, 466)
                ))
                .build();



    }

}