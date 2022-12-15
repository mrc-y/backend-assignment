package com.katanox.api.unittest.booking.service;

import com.katanox.api.booking.exception.BookingException;
import com.katanox.api.booking.mapper.BookingMapper;
import com.katanox.api.booking.payload.BookingRequest;
import com.katanox.api.booking.payload.BookingResponse;
import com.katanox.api.booking.repository.BookingRepository;
import com.katanox.api.booking.service.BookingSenderService;
import com.katanox.api.booking.service.BookingService;
import com.katanox.api.booking.validation.BookingRequestValidator;
import com.katanox.api.search.exception.DatesNotValidException;
import com.katanox.api.search.payload.RoomPriceResponse;
import com.katanox.api.search.repository.PriceRepository;
import com.katanox.api.search.service.ExtraChargeCalculationService;
import com.katanox.api.search.service.SearchService;
import com.katanox.api.search.service.TaxCalculationService;
import com.katanox.test.sql.enums.BookingStatus;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @InjectMocks
    private BookingService bookingService;

    @Mock
    private BookingRequestValidator bookingRequestValidator;
    @Mock
    private BookingSenderService bookingSenderService;
    @Mock
    private SearchService searchService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ExtraChargeCalculationService extraChargeCalculationService;
    @Mock
    private TaxCalculationService taxCalculationService;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private PriceRepository priceRepository;

    @Test
    void book() throws Exception {
        var roomPriceResponse = new RoomPriceResponse(1, 1, BigDecimal.valueOf(100), "EUR");
        when(searchService.searchRoomAvailability(any(), any(), any())).thenReturn(roomPriceResponse);
        when(bookingRepository.insertBooking(any(), any())).thenReturn(123L);
        when(extraChargeCalculationService.calculateAllExtraCharges(any(),any(),any(),any(),any(),any())).thenReturn(BigDecimal.valueOf(100));
        when(taxCalculationService.calculateBeforeTaxPrice(any(), any())).thenReturn(BigDecimal.TEN);
        BookingRequest bookingRequest = getBookingRequest();
        BookingResponse bookingResponse = bookingService.book(bookingRequest);

        assertEquals(123L, bookingResponse.getBookingId());
        assertEquals(BookingStatus.in_progress, bookingResponse.getBookingStatus());
    }
    private BookingRequest getBookingRequest() {
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setCheckin(LocalDate.now().minusDays(3));
        bookingRequest.setCheckout(LocalDate.now().minusDays(1));
        bookingRequest.setRoomId(1L);
        bookingRequest.setHotelId(2L);
        bookingRequest.setPrice(BigDecimal.valueOf(200));
        return bookingRequest;
    }

}