package com.katanox.api.booking.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class BookingDTO {

    private long bookingId;
    private long hotelId;
    private long roomId;
    private BigDecimal priceBeforeTax;
    private BigDecimal priceAfterTax;
    private String currency;
    private Guest guest;
    private Payment payment;
}
