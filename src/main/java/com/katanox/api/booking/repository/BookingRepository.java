package com.katanox.api.booking.repository;

import com.katanox.api.booking.payload.BookingRequest;
import com.katanox.api.booking.payload.Guest;
import com.katanox.test.sql.enums.BookingStatus;
import com.katanox.test.sql.tables.records.BookingsRecord;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

import static com.katanox.test.sql.Tables.BOOKINGS;

@Repository
@Slf4j
public class BookingRepository {

    private final DSLContext dsl;

    public BookingRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Long insertBooking(BookingRequest bookingRequest, BigDecimal priceBeforeTax) {
        BookingsRecord bookingsRecord = dsl.newRecord(BOOKINGS);
        Guest guest = bookingRequest.getGuest();
        bookingsRecord.setName(guest.getName());
        bookingsRecord.setSurname(guest.getSurname());
        bookingsRecord.setBirthdate(guest.getBirthdate());
        // we won't store credit card information in the db, because of security reasons
        bookingsRecord.setHotelId(bookingRequest.getHotelId());
        bookingsRecord.setRoomId(bookingRequest.getRoomId());
        bookingsRecord.setCheckIn(bookingRequest.getCheckin());
        bookingsRecord.setCheckOut(bookingRequest.getCheckout());
        bookingsRecord.setPriceAfterTax(bookingRequest.getPrice());
        bookingsRecord.setPriceBeforeTax(priceBeforeTax);
        bookingsRecord.setCurrency(bookingRequest.getCurrency());
        bookingsRecord.setBookingStatus(BookingStatus.in_progress);
        bookingsRecord.store();
        var id = bookingsRecord.getId();
        log.info("A booking is inserted with booking id: {}", id);
        return id;
    }

    public BookingsRecord getBooking(Long bookingId) {
        return dsl.selectFrom(BOOKINGS).where(BOOKINGS.ID.eq(bookingId)).fetchOne();
    }

    public void updateBookingStatus(Long bookingId, BookingStatus bookingStatus) {
        dsl.update(BOOKINGS).set(BOOKINGS.BOOKING_STATUS, bookingStatus).where(BOOKINGS.ID.eq(bookingId)).execute();
        log.info("Booking status is updated to {} for booking id: {}", bookingStatus, bookingId);
    }
}
