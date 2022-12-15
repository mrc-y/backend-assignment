package com.katanox.api.search.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class SearchException extends Exception {

    private final HttpStatus statusCode;

    public SearchException(String errorMessage, HttpStatus statusCode) {
        super(errorMessage);
        this.statusCode = statusCode;
    }
}
