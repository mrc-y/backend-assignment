package com.katanox.api.search.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomPriceResponse {

    private long hotelId;
    private long roomId;
    private BigDecimal price;
    private String currency;
}
