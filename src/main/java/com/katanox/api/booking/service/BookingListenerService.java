package com.katanox.api.booking.service;

import com.katanox.api.booking.payload.BookingResponse;
import com.katanox.api.search.repository.PriceRepository;
import com.katanox.test.sql.enums.BookingStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "${katanox.rabbitmq.queue}", id = "listener")
@Slf4j
public class BookingListenerService {

    private final PriceRepository priceRepository;

    private final BookingService bookingService;

    public BookingListenerService(PriceRepository priceRepository, BookingService bookingService) {
        this.priceRepository = priceRepository;
        this.bookingService = bookingService;
    }

    @RabbitHandler
    public void receiver(BookingResponse bookingResponse) {
        log.info("Booking response is received. Details: {}", bookingResponse.toString());
        if (bookingResponse.getBookingStatus().equals(BookingStatus.failed)) {
            // if booking is failed, then we should reflect this to available room quantity in db
            priceRepository.increaseQuantityForRoom(bookingResponse.getBookingId());
        }
        bookingService.updateBookingStatus(bookingResponse.getBookingId(), bookingResponse.getBookingStatus());
    }
}
