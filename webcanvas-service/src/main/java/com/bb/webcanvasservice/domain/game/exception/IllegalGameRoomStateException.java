package com.bb.webcanvasservice.domain.game.exception;

public class IllegalGameRoomStateException extends RuntimeException {
    public IllegalGameRoomStateException(String message) {
        super(message);
    }
}
