package com.katanox.api.booking.service;

import com.katanox.api.booking.payload.BookingDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BookingSenderService {

    private final AmqpTemplate template;

    private final Queue queue;

    public BookingSenderService(AmqpTemplate template, Queue queue) {
        this.template = template;
        this.queue = queue;
    }

    public void ObjectRabbitMQSender(BookingDTO bookingDTO) {
        log.info("Sending booking data to messaging queue: {} for booking id: {}", queue, bookingDTO.getBookingId());
        template.convertAndSend(queue.getName(), bookingDTO);
    }
}
