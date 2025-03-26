package com.bb.webcanvasservice.domain.game.exception;

public class AlreadyEnteredRoomException extends RuntimeException {
    public AlreadyEnteredRoomException(String message) {
        super(message);
    }
}
