package com.katanox.api.booking.exception;

import org.springframework.http.HttpStatus;

public class PriceNotMatchedException extends BookingException {

    public PriceNotMatchedException(String errorMessage) {
        super(errorMessage, HttpStatus.CONFLICT);
    }
}
