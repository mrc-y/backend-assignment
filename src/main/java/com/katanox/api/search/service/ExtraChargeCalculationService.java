package com.katanox.api.search.service;

import com.katanox.api.search.repository.ExtraChargesFlatRepository;
import com.katanox.api.search.repository.ExtraChargesPercentageRepository;
import com.katanox.api.search.repository.PriceRepository;
import com.katanox.test.sql.enums.AppliedOn;
import com.katanox.test.sql.enums.ChargeType;
import com.katanox.test.sql.tables.records.ExtraChargesFlatRecord;
import com.katanox.test.sql.tables.records.ExtraChargesPercentageRecord;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ExtraChargeCalculationService {

    private final ExtraChargesPercentageRepository extraChargesPercentageRepository;
    private final ExtraChargesFlatRepository extraChargesFlatRepository;
    private final PriceRepository priceRepository;
    private final TaxCalculationService taxCalculationService;


    public ExtraChargeCalculationService(ExtraChargesPercentageRepository extraChargesPercentageRepository,
                                         ExtraChargesFlatRepository extraChargesFlatRepository,
                                         PriceRepository priceRepository, TaxCalculationService taxCalculationService) {
        this.extraChargesPercentageRepository = extraChargesPercentageRepository;
        this.extraChargesFlatRepository = extraChargesFlatRepository;
        this.priceRepository = priceRepository;
        this.taxCalculationService = taxCalculationService;
    }

    public BigDecimal calculateFlatCharges(Long hotelId, String currency, Integer days) {
        List<ExtraChargesFlatRecord> extraChargesForHotel = extraChargesFlatRepository.getExtraChargesForHotel(hotelId);
        BigDecimal totalExtraCharge = BigDecimal.ZERO;
        for (ExtraChargesFlatRecord extraChargesFlatRecord : extraChargesForHotel) {
            BigDecimal flatPrice = BigDecimal.valueOf(extraChargesFlatRecord.getPrice());
            if (extraChargesFlatRecord.getChargeType().equals(ChargeType.once)) {
                // todo: here we should convert the other currencies to our currency
                // I'm skipping this for now to keep it simple
                totalExtraCharge = totalExtraCharge.add(flatPrice);

            } else if (extraChargesFlatRecord.getChargeType().equals(ChargeType.per_night)) {
                totalExtraCharge = totalExtraCharge.add(flatPrice.multiply(BigDecimal.valueOf(days)));
            }
        }
        return totalExtraCharge;
    }

    public BigDecimal calculatePercentageCharges(Long hotelId, BigDecimal firstNightCharge, BigDecimal totalPrice) {
        List<ExtraChargesPercentageRecord> extraChargesForHotel = extraChargesPercentageRepository.getExtraChargesForHotel(
                hotelId);
        BigDecimal totalExtraCharge = BigDecimal.ZERO;
        for (ExtraChargesPercentageRecord extraChargesRecord : extraChargesForHotel) {
            BigDecimal percentage =
                    BigDecimal.valueOf(extraChargesRecord.getPercentage()).divide(BigDecimal.valueOf(100));
            if (extraChargesRecord.getAppliedOn().equals(AppliedOn.first_night)) {
                totalExtraCharge = totalExtraCharge.add(firstNightCharge.multiply(percentage));
            } else if (extraChargesRecord.getAppliedOn().equals(AppliedOn.total_amount)) {
                totalExtraCharge = totalExtraCharge.add(totalPrice.multiply(percentage));
            }
        }
        return totalExtraCharge;
    }

    public BigDecimal calculateAllExtraCharges(Long hotelId, Long roomId, LocalDate checkInDate, String currency, Integer days,
                                               BigDecimal totalPrice) {
        BigDecimal firstNightCharge = priceRepository.getPrice(roomId, checkInDate);
        BigDecimal firstNightChargeBeforeTax = taxCalculationService.calculateBeforeTaxPrice(hotelId,
                firstNightCharge);
        BigDecimal totalPriceBeforeTax = taxCalculationService.calculateBeforeTaxPrice(hotelId,
                totalPrice);
        BigDecimal flatCharges = calculateFlatCharges(hotelId, currency, days);
        BigDecimal percentageCharges = calculatePercentageCharges(hotelId, firstNightChargeBeforeTax, totalPriceBeforeTax);
        return flatCharges.add(percentageCharges);
    }
}
