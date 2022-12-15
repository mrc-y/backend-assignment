package com.katanox.api.booking.exception;

import org.springframework.http.HttpStatus;

public class RoomNotAvailableException extends BookingException {
    public RoomNotAvailableException(String errorMessage) {
        super(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
