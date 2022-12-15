package com.katanox.api.booking.service;

import com.katanox.api.booking.exception.BookingException;
import com.katanox.api.booking.exception.PriceNotMatchedException;
import com.katanox.api.booking.exception.RoomNotAvailableException;
import com.katanox.api.booking.mapper.BookingMapper;
import com.katanox.api.booking.payload.BookingDTO;
import com.katanox.api.booking.payload.BookingDetailResponse;
import com.katanox.api.booking.payload.BookingRequest;
import com.katanox.api.booking.payload.BookingResponse;
import com.katanox.api.booking.repository.BookingRepository;
import com.katanox.api.booking.validation.BookingRequestValidator;
import com.katanox.api.search.exception.DatesNotValidException;
import com.katanox.api.search.payload.RoomPriceResponse;
import com.katanox.api.search.repository.PriceRepository;
import com.katanox.api.search.service.ExtraChargeCalculationService;
import com.katanox.api.search.service.SearchService;
import com.katanox.api.search.service.TaxCalculationService;
import com.katanox.test.sql.enums.BookingStatus;
import com.katanox.test.sql.tables.records.BookingsRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class BookingService {

    private final BookingRequestValidator bookingRequestValidator;
    private final BookingSenderService bookingSenderService;
    private final SearchService searchService;
    private final BookingRepository bookingRepository;
    private final ExtraChargeCalculationService extraChargeCalculationService;
    private final TaxCalculationService taxCalculationService;
    private final BookingMapper bookingMapper;
    private final PriceRepository priceRepository;

    public BookingService(BookingRequestValidator bookingRequestValidator, BookingSenderService bookingSenderService,
                          SearchService searchService, BookingRepository bookingRepository,
                          ExtraChargeCalculationService extraChargeCalculationService,
                          TaxCalculationService taxCalculationService, BookingMapper bookingMapper,
                          PriceRepository priceRepository) {
        this.bookingRequestValidator = bookingRequestValidator;
        this.bookingSenderService = bookingSenderService;
        this.searchService = searchService;
        this.bookingRepository = bookingRepository;
        this.extraChargeCalculationService = extraChargeCalculationService;
        this.taxCalculationService = taxCalculationService;
        this.bookingMapper = bookingMapper;
        this.priceRepository = priceRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public BookingResponse book(BookingRequest bookingRequest) throws BookingException, DatesNotValidException {
        bookingRequestValidator.validate(bookingRequest);
        RoomPriceResponse roomPriceResponse = searchService.searchRoomAvailability(bookingRequest.getRoomId(),
                bookingRequest.getCheckin(), bookingRequest.getCheckout());
        if (roomPriceResponse == null) {
            throw new RoomNotAvailableException("Room is not available!");
        }
        BigDecimal allExtraCharges = getAllExtraCharges(bookingRequest, roomPriceResponse);
        BigDecimal totalPrice = roomPriceResponse.getPrice().add(allExtraCharges);
        if (!totalPrice.equals(bookingRequest.getPrice())) {
            throw new PriceNotMatchedException("Price has changed!");
        }
        BigDecimal priceBeforeTax = getPriceBeforeTax(bookingRequest, roomPriceResponse, allExtraCharges);
        // persist booking and get id
        var bookingId = bookingRepository.insertBooking(bookingRequest, priceBeforeTax);
        // since the room is being booked, we need to reflect this to the available room quantity in db
        priceRepository.decreaseQuantityForRoom(bookingRequest.getRoomId(), bookingRequest.getCheckin(),
                bookingRequest.getCheckout());
        // Map to BookingDTO
        BookingDTO bookingDTO = bookingMapper.map(bookingRequest, priceBeforeTax, bookingId);
        bookingSenderService.ObjectRabbitMQSender(bookingDTO);
        return new BookingResponse(bookingId, BookingStatus.in_progress);
    }

    private BigDecimal getPriceBeforeTax(BookingRequest bookingRequest, RoomPriceResponse roomPriceResponse,
                                         BigDecimal allExtraCharges) {
        BigDecimal priceBeforeTaxWithoutExtras = taxCalculationService.calculateBeforeTaxPrice(
                bookingRequest.getHotelId(), roomPriceResponse.getPrice());
        return priceBeforeTaxWithoutExtras.add(allExtraCharges);
    }

    private BigDecimal getAllExtraCharges(BookingRequest bookingRequest, RoomPriceResponse roomPriceResponse) {
        var daysCount = searchService.calculateDaysCount(bookingRequest.getCheckin(), bookingRequest.getCheckout());
        return extraChargeCalculationService.calculateAllExtraCharges(bookingRequest.getHotelId(),
                bookingRequest.getRoomId(), bookingRequest.getCheckin(), bookingRequest.getCurrency(), daysCount,
                roomPriceResponse.getPrice());
    }

    public void updateBookingStatus(Long bookingId, BookingStatus bookingStatus) {
        bookingRepository.updateBookingStatus(bookingId, bookingStatus);
    }

    public BookingDetailResponse getBooking(Long bookingId) {
        BookingsRecord bookingsRecord = bookingRepository.getBooking(bookingId);
        return bookingMapper.map(bookingsRecord);
    }
}
