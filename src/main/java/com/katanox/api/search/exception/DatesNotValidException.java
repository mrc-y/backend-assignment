package com.katanox.api.search.exception;

import org.springframework.http.HttpStatus;

public class DatesNotValidException extends SearchException {
    public DatesNotValidException(String errorMessage) {
        super(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
