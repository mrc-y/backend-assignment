package com.katanox.api.booking.validation;

import com.katanox.api.booking.exception.CardNotValidException;
import com.katanox.api.booking.payload.BookingRequest;
import com.katanox.api.search.exception.DatesNotValidException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class BookingRequestValidator {

    public void validate(BookingRequest bookingRequest) throws DatesNotValidException, CardNotValidException {
        if (bookingRequest.getCheckin().isAfter(bookingRequest.getCheckout())) {
            throw new DatesNotValidException("Check in date cannot be earlier than check out!");
        }
        if (StringUtils.isNumericSpace(bookingRequest.getPayment().getCardNumber())){
            throw new CardNotValidException("Card number should not have characters or symbols!");
        }
    }
}
