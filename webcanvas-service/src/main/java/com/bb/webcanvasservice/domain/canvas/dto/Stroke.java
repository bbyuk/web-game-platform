package com.bb.webcanvasservice.domain.canvas.dto;

import java.util.List;

/**
 * 캔버스에 그림을 그릴 때 발생하는 스트로크 메세지
 * @param color
 * @param lineWidth
 * @param points
 */
public record Stroke(
        // 스트로크의 색상
        String color,
        // 선의 두께
        int lineWidth,
        // 선을 구성하는 점들
        List<Point> points
) {
    public record Point(double x, double y) {}
}
