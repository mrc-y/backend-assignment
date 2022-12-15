package com.katanox.api.search.service;

import com.katanox.api.search.repository.HotelRepository;
import com.katanox.test.sql.tables.records.HotelsRecord;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class TaxCalculationService {

    private final HotelRepository hotelRepository;

    public TaxCalculationService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public BigDecimal calculateBeforeTaxPrice(Long hotelId, BigDecimal afterTaxPrice) {
        HotelsRecord hotel = hotelRepository.getHotel(hotelId);
        BigDecimal vat = hotel.getVat();
        return afterTaxPrice.multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(100).add(vat), RoundingMode.CEILING);
    }
}
