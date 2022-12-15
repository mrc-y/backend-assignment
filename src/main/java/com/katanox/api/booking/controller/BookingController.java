package com.katanox.api.booking.controller;


import com.katanox.api.booking.exception.BookingException;
import com.katanox.api.booking.payload.BookingDetailResponse;
import com.katanox.api.booking.payload.BookingRequest;
import com.katanox.api.booking.payload.BookingResponse;
import com.katanox.api.booking.service.BookingService;
import com.katanox.api.search.exception.DatesNotValidException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("booking")
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping(path = "/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<BookingResponse> booking(
            @RequestBody @Valid BookingRequest request) throws BookingException, DatesNotValidException {
        log.info("Booking request has arrived for hotel id: {}, room id: {} for dates between {}-{}",
                request.getHotelId(), request.getRoomId(), request.getCheckin(), request.getCheckout());
        var response = bookingService.book(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(path = "/{bookingId}", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<BookingDetailResponse> getBooking(@PathVariable("bookingId") Long bookingId) {
        log.info("Booking detail is requested for booking id: {}", bookingId);
        var response = bookingService.getBooking(bookingId);
        return new ResponseEntity<>(response, response == null ? HttpStatus.NO_CONTENT : HttpStatus.OK);
    }


}