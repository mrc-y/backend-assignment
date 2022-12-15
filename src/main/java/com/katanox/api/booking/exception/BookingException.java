package com.katanox.api.booking.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BookingException extends Exception {

    private final HttpStatus statusCode;

    public BookingException(String errorMessage, HttpStatus statusCode) {
        super(errorMessage);
        this.statusCode = statusCode;
    }
}
