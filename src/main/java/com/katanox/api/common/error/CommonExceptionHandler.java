package com.katanox.api.common.error;

import com.katanox.api.booking.exception.BookingException;
import com.katanox.api.booking.exception.CardNotValidException;
import com.katanox.api.booking.exception.PriceNotMatchedException;
import com.katanox.api.booking.exception.RoomNotAvailableException;
import com.katanox.api.search.exception.DatesNotValidException;
import com.katanox.api.search.exception.SearchException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

@Slf4j
@RestControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler({CardNotValidException.class,
            PriceNotMatchedException.class, RoomNotAvailableException.class})
    public ResponseEntity<Object> handleProviderResponseException(BookingException ex) {
        log.error("Exception occurred: {}", ex.toString());
        return buildErrorResponse(ex.getStatusCode(), ex.getMessage());
    }

    @ExceptionHandler(DatesNotValidException.class)
    public ResponseEntity<Object> handleProviderResponseException(SearchException ex) {
        log.error("Exception occurred: {}", ex.toString());
        return buildErrorResponse(ex.getStatusCode(), ex.getMessage());
    }

    private ResponseEntity<Object> buildErrorResponse(HttpStatus httpStatus, String errorMessage) {
        return ResponseEntity.status(httpStatus)
                .body(ErrorResponse.builder()
                        .status(httpStatus.value())
                        .error(errorMessage)
                        .timestamp(OffsetDateTime.now())
                        .build());
    }
}
