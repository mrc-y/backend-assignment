package com.katanox.api.search.repository;

import com.katanox.api.search.payload.RoomPriceResponse;
import com.katanox.api.search.payload.SearchRequest;
import com.katanox.test.sql.tables.records.PricesRecord;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.katanox.test.sql.Tables.BOOKINGS;
import static com.katanox.test.sql.Tables.PRICES;
import static com.katanox.test.sql.Tables.ROOMS;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.sum;

@Repository
@Slf4j
public class PriceRepository {

    private final DSLContext dsl;

    public PriceRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public List<RoomPriceResponse> searchPricesForCertainDaysWithHotelId(SearchRequest searchRequest,
                                                                         Integer daysCount) {

        return dsl.select(ROOMS.HOTEL_ID, PRICES.ROOM_ID, PRICES.CURRENCY, sum(PRICES.PRICE_AFTER_TAX))
                .from(PRICES)
                .join(ROOMS)
                .on(ROOMS.ID.eq(PRICES.ROOM_ID))
                .where(ROOMS.HOTEL_ID.eq(searchRequest.getHotelId()))
                .and(PRICES.DATE.ge(searchRequest.getCheckin()))
                .and(PRICES.DATE.lt(searchRequest.getCheckout()))
                .and(PRICES.QUANTITY.ge(1))
                .groupBy(ROOMS.HOTEL_ID, PRICES.ROOM_ID, PRICES.CURRENCY)
                // this will ensure that the room is available for all the given days
                .having(count(PRICES.ROOM_ID).eq(daysCount))
                .fetch()
                .map(response -> RoomPriceResponse.builder()
                        .hotelId(response.value1())
                        .roomId(response.value2())
                        .price(response.value4())
                        .currency(response.value3())
                        .build());
    }

    public RoomPriceResponse searchPricesForCertainDaysWithRoomId(Long roomId, LocalDate checkIn, LocalDate checkOut,
                                                                  Integer daysCount) {
        return dsl.select(PRICES.ROOM_ID, PRICES.CURRENCY, sum(PRICES.PRICE_AFTER_TAX))
                .from(PRICES)
                .where(PRICES.ROOM_ID.eq(roomId))
                .and(PRICES.DATE.ge(checkIn))
                .and(PRICES.DATE.lt(checkOut))
                .and(PRICES.QUANTITY.ge(1))
                .groupBy(PRICES.ROOM_ID, PRICES.CURRENCY)
                // this will ensure that the room is available for all the given days
                .having(count(PRICES.ROOM_ID).eq(daysCount))
                .fetchOne(response -> RoomPriceResponse.builder()
                        .roomId(response.value1())
                        .price(response.value3())
                        .currency(response.value2())
                        .build());
    }

    public BigDecimal getPrice(Long roomId, LocalDate date) {
        return dsl.selectFrom(PRICES)
                .where(PRICES.ROOM_ID.eq(roomId))
                .and(PRICES.DATE.eq(date))
                .fetchOne(PricesRecord::getPriceAfterTax);
    }

    public void increaseQuantityForRoom(Long bookingId) {
        dsl.update(PRICES.join(BOOKINGS).on(PRICES.ROOM_ID.eq(BOOKINGS.ROOM_ID)))
                .set(PRICES.QUANTITY, PRICES.QUANTITY.plus(1))
                .where(BOOKINGS.ID.eq(bookingId))
                .and(PRICES.ROOM_ID.eq(BOOKINGS.ROOM_ID))
                .and(PRICES.DATE.ge(BOOKINGS.CHECK_IN))
                .and(PRICES.DATE.lt(BOOKINGS.CHECK_OUT))
                .execute();
        log.info("Room quantity is increased for booking: {}", bookingId);
    }

    public void decreaseQuantityForRoom(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        dsl.update(PRICES)
                .set(PRICES.QUANTITY, PRICES.QUANTITY.minus(1))
                .where(PRICES.ROOM_ID.eq(roomId))
                .and(PRICES.DATE.ge(checkIn))
                .and(PRICES.DATE.lt(checkOut))
                .execute();
        log.info("Room quantity is decreased for room {} between days: {}-{}", roomId, checkIn, checkOut);

    }
}
