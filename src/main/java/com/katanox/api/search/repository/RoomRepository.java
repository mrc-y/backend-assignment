package com.katanox.api.search.repository;

import com.katanox.test.sql.tables.records.RoomsRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.katanox.test.sql.Tables.ROOMS;

@Repository
public class RoomRepository {

    private final DSLContext dsl;

    public RoomRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public List<RoomsRecord> findRooms(Long hotelId){
        return dsl.select()
                .from(ROOMS)
                .where(ROOMS.HOTEL_ID.eq(hotelId))
                .fetchInto(RoomsRecord.class);
    }

    public List<Long> findRoomIds(Long hotelId){
        return dsl.selectFrom(ROOMS)
                .where(ROOMS.HOTEL_ID.eq(hotelId))
                .fetch(RoomsRecord::getId);
    }

}
