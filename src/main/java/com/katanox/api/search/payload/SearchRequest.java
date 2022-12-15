package com.katanox.api.search.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {
    @NotNull(message = "Check in date should be provided")
    private LocalDate checkin;
    @NotNull(message = "Check out date should be provided")
    private LocalDate checkout;
    @NotNull(message = "Hotel id should be provided")
    private Long hotelId;
}