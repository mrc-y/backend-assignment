package com.katanox.api.booking.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
public class Payment {
    @NotEmpty(message = "Card holder should be provided")
    private String cardHolder;
    @NotEmpty(message = "Card number should be provided")
    private String cardNumber;
    @NotEmpty(message = "Card cvv should be provided")
    private String cvv;
    @NotEmpty(message = "Card expiry month should be provided")
    private String expiryMonth;
    @NotEmpty(message = "Card expiry year should be provided")
    private String expiryYear;
}
