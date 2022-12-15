package com.katanox.api.search.repository;

import com.katanox.test.sql.tables.records.ExtraChargesPercentageRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.katanox.test.sql.Tables.EXTRA_CHARGES_PERCENTAGE;

@Repository
public class ExtraChargesPercentageRepository {

    private final DSLContext dsl;

    public ExtraChargesPercentageRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public List<ExtraChargesPercentageRecord> getExtraChargesForHotel(Long hotelId) {
        return dsl.select()
                .from(EXTRA_CHARGES_PERCENTAGE)
                .where(EXTRA_CHARGES_PERCENTAGE.HOTEL_ID.eq(hotelId))
                .fetchInto(ExtraChargesPercentageRecord.class);
    }
}
