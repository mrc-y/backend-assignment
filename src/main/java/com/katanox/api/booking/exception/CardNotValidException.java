package com.katanox.api.booking.exception;

import org.springframework.http.HttpStatus;

public class CardNotValidException extends BookingException {
    public CardNotValidException(String errorMessage) {
        super(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
