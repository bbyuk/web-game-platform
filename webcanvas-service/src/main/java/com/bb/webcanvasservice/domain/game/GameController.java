package com.bb.webcanvasservice.domain.game;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("game/canvas")
@RequiredArgsConstructor
public class GameController {

    private final GameRoomService gameRoomService;



}
