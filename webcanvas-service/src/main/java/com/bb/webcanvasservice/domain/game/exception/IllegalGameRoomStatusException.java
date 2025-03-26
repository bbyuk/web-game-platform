package com.bb.webcanvasservice.domain.game.exception;

public class IllegalGameRoomStatusException extends RuntimeException {
    public IllegalGameRoomStatusException(String message) {
        super(message);
    }
}
