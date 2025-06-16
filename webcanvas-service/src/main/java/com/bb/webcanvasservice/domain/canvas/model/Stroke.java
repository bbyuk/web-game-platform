package com.bb.webcanvasservice.domain.canvas.model;

import java.util.List;

/**
 * 캔버스에 그림을 그릴 때 발생하는 스트로크 모델
 */
public class Stroke {
    private final Long id;

    // 스트로크의 색상
    private final String color;

    // 선의 두께
    private final int lineWidth;

    // 선을 구성하는 점들
    private final List<Coordinate> points;

    public Stroke(Long id, String color, int lineWidth, List<Coordinate> points) {
        this.id = id;
        this.color = color;
        this.lineWidth = lineWidth;
        this.points = points;
    }

    public static Stroke createNewStroke(String color, int lineWidth, List<Coordinate> points) {
        return new Stroke(null, color, lineWidth, points);
    }
}
