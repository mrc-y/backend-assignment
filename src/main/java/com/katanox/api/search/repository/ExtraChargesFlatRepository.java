package com.katanox.api.search.repository;

import com.katanox.test.sql.tables.records.ExtraChargesFlatRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.katanox.test.sql.Tables.EXTRA_CHARGES_FLAT;

@Repository
public class ExtraChargesFlatRepository {

    private final DSLContext dsl;

    public ExtraChargesFlatRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public List<ExtraChargesFlatRecord> getExtraChargesForHotel(Long hotelId) {
        return dsl.select()
                .from(EXTRA_CHARGES_FLAT)
                .where(EXTRA_CHARGES_FLAT.HOTEL_ID.eq(hotelId))
                .fetchInto(ExtraChargesFlatRecord.class);
    }
}
