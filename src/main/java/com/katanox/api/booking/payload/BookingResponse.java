package com.katanox.api.booking.payload;

import com.katanox.test.sql.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private long bookingId;
    private BookingStatus bookingStatus;
}




