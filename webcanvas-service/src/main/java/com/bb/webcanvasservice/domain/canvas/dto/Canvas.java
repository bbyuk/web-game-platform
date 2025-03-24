package com.bb.webcanvasservice.domain.canvas.dto;

import java.util.List;

public class Canvas {

    // 현재 캔버스 ID
    private Long canvasId;

    // 캔버스와 N:1 매핑되어 있는 게임 ID
    // 한 게임(방)에는 여러 캔버스가 존재할 수 있음 -> 여러판 / 동시에는 존재할 수 없음.
    private Long gameId;

    // 현재 캔버스에 그려져있는 모든 Stroke
    private List<Stroke> strokes;
}
