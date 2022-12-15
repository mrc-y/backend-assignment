package com.katanox.api.booking.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class BookingRequest {

    @NotNull(message = "Hotel id should be provided")
    private Long hotelId;
    @NotNull(message = "Room id should be provided")
    private Long roomId;
    @NotNull(message = "Price should be provided")
    private BigDecimal price;
    @NotEmpty(message = "Currency should be provided")
    private String currency;
    private Guest guest;
    private Payment payment;
    @NotNull(message = "Check in date should be provided")
    private LocalDate checkin;
    @NotNull(message = "Check out date should be provided")
    private LocalDate checkout;
}
