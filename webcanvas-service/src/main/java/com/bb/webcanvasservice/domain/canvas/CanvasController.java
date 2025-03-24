package com.bb.webcanvasservice.domain.canvas;


import com.bb.webcanvasservice.domain.canvas.dto.Stroke;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("canvas")
@RequiredArgsConstructor
public class CanvasController {

    private final CanvasService canvasService;

    /**
     * 화면에서 그린 stroke를 현재 같은 방에 접속해 있는 유저들에게 브로드캐스팅한다.
     * @param stroke
     * @return
     */
    @PostMapping("drawing/stroke")
    public ResponseEntity<Stroke> drawStroke(@RequestBody Stroke stroke) {
        return ResponseEntity.ok(stroke);
    }

}
