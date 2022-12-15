package com.katanox.api.booking.mapper;

import com.katanox.api.booking.payload.BookingDTO;
import com.katanox.api.booking.payload.BookingDetailResponse;
import com.katanox.api.booking.payload.BookingRequest;
import com.katanox.api.booking.payload.Guest;
import com.katanox.test.sql.tables.records.BookingsRecord;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BookingMapper {

    public BookingDTO map(BookingRequest bookingRequest, BigDecimal priceBeforeTax, Long bookingId) {
        if(bookingRequest == null || bookingId == null) {
            return null;
        }
        var bookingDTO = new BookingDTO();
        BeanUtils.copyProperties(bookingRequest, bookingDTO);
        bookingDTO.setPriceAfterTax(bookingRequest.getPrice());
        bookingDTO.setPriceBeforeTax(priceBeforeTax);
        bookingDTO.setBookingId(bookingId);
        return bookingDTO;
    }

    public BookingDetailResponse map(BookingsRecord bookingsRecord) {
        if (bookingsRecord == null) {
            return null;
        }
        var bookingDetailResponse = new BookingDetailResponse();
        bookingDetailResponse.setBookingId(bookingsRecord.getId());
        bookingDetailResponse.setPrice(bookingsRecord.getPriceAfterTax());
        BeanUtils.copyProperties(bookingsRecord, bookingDetailResponse);
        bookingDetailResponse.setGuest(mapGuest(bookingsRecord));
        return bookingDetailResponse;
    }

    private Guest mapGuest(BookingsRecord bookingsRecord) {
        var guest = new Guest();
        BeanUtils.copyProperties(bookingsRecord, guest);
        return guest;
    }

}
