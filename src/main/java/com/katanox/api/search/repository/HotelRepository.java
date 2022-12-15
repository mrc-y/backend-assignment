package com.katanox.api.search.repository;

import com.katanox.test.sql.tables.records.HotelsRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static com.katanox.test.sql.Tables.HOTELS;

@Repository
public class HotelRepository {

    private final DSLContext dsl;

    public HotelRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public HotelsRecord getHotel(Long hotelId){
        return dsl.selectFrom(HOTELS)
                .where(HOTELS.ID.eq(hotelId))
                .fetchOneInto(HotelsRecord.class);
    }
}
