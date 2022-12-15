package com.katanox.api.search.service;

import com.katanox.api.search.exception.DatesNotValidException;
import com.katanox.api.search.payload.RoomPriceResponse;
import com.katanox.api.search.payload.SearchRequest;
import com.katanox.api.search.payload.SearchResponse;
import com.katanox.api.search.repository.PriceRepository;
import com.katanox.api.search.validation.SearchRequestValidator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class SearchService {

    private final SearchRequestValidator searchRequestValidator;

    private final PriceRepository priceRepository;
    private final ExtraChargeCalculationService extraChargeCalculationService;

    public SearchService(SearchRequestValidator searchRequestValidator, PriceRepository priceRepository,
                         ExtraChargeCalculationService extraChargeCalculationService) {
        this.searchRequestValidator = searchRequestValidator;
        this.priceRepository = priceRepository;
        this.extraChargeCalculationService = extraChargeCalculationService;
    }

    public SearchResponse search(SearchRequest searchRequest) throws DatesNotValidException {
        searchRequestValidator.validate(searchRequest);
        var daysCount = calculateDaysCount(searchRequest.getCheckin(), searchRequest.getCheckout());
        List<RoomPriceResponse> roomPriceResponses = priceRepository.searchPricesForCertainDaysWithHotelId(
                searchRequest, daysCount);

        roomPriceResponses.forEach(roomPriceResponse -> {
            BigDecimal allExtraCharges = extraChargeCalculationService.calculateAllExtraCharges(
                    roomPriceResponse.getHotelId(), roomPriceResponse.getRoomId(), searchRequest.getCheckin(),
                    roomPriceResponse.getCurrency(), daysCount, roomPriceResponse.getPrice());
            roomPriceResponse.setPrice(roomPriceResponse.getPrice().add(allExtraCharges));
        });
        // returns empty list inside SearchResponse if there is no available rooms
        return new SearchResponse(roomPriceResponses);
    }

    public RoomPriceResponse searchRoomAvailability(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        var daysCount = calculateDaysCount(checkIn, checkOut);
        return priceRepository.searchPricesForCertainDaysWithRoomId(roomId, checkIn, checkOut, daysCount);
    }

    public Integer calculateDaysCount(LocalDate checkIn, LocalDate checkOut) {
        Long daysCountLong = ChronoUnit.DAYS.between(checkIn, checkOut);
        return daysCountLong.intValue();
    }
}
