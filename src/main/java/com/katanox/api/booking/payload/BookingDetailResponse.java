package com.katanox.api.booking.payload;

import com.katanox.test.sql.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDetailResponse {

    private long bookingId;
    private long hotelId;
    private long roomId;
    private BigDecimal price;
    private String currency;
    private Guest guest;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private BookingStatus bookingStatus;
}
