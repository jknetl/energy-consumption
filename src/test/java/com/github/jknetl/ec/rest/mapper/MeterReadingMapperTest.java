package com.github.jknetl.ec.rest.mapper;

import com.github.jknetl.ec.TestEntityFactory;
import com.github.jknetl.ec.data.model.EnergyUnit;
import com.github.jknetl.ec.data.model.Location;
import com.github.jknetl.ec.data.model.Meter;
import com.github.jknetl.ec.data.model.MeterReading;
import com.github.jknetl.ec.data.model.Tenant;
import com.github.jknetl.ec.rest.dto.MeterReadingRequest;
import com.github.jknetl.ec.rest.dto.MeterReadingResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MeterReadingMapperTest {

    private final MeterReadingMapper mapper = new MeterReadingMapperImpl();

    @Test
    void map_whenAllArgsProvided_shouldMapValueUnitTenantAndId() {
        Tenant tenant = TestEntityFactory.createTenantA();
        MeterReadingRequest request = MeterReadingRequest.builder()
                .value(new BigDecimal("250.50"))
                .unit(EnergyUnit.CUBIC_METER)
                .build();

        MeterReading result = mapper.map(tenant, 99L, request);

        assertThat(result.getTenant()).isEqualTo(tenant);
        assertThat(result.getId()).isEqualTo(99L);
        assertThat(result.getValue()).isEqualByComparingTo(new BigDecimal("250.50"));
        assertThat(result.getUnit()).isEqualTo(EnergyUnit.CUBIC_METER);
    }

    @Test
    void map_whenAllArgsProvided_shouldNotSetMeter() {
        Tenant tenant = TestEntityFactory.createTenantA();
        MeterReadingRequest request = MeterReadingRequest.builder()
                .value(new BigDecimal("100.00"))
                .unit(EnergyUnit.KWH)
                .build();

        MeterReading result = mapper.map(tenant, 1L, request);

        assertThat(result.getMeter()).isNull();
    }

    @Test
    void map_whenAllArgsNull_shouldReturnNull() {
        MeterReading result = mapper.map((Tenant) null, null, (MeterReadingRequest) null);

        assertThat(result).isNull();
    }

    @Test
    void map_whenReadingHasMeter_shouldExtractMeterId() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        Meter meter = TestEntityFactory.createSavedMeter(tenant, location);
        MeterReading reading = TestEntityFactory.createSavedMeterReading(tenant, meter);

        MeterReadingResponse result = mapper.map(reading);

        assertThat(result.meterId()).isEqualTo(meter.getId());
    }

    @Test
    void map_whenReadingMeterIsNull_shouldHaveNullMeterId() {
        Tenant tenant = TestEntityFactory.createTenantA();
        MeterReading reading = TestEntityFactory.createSavedMeterReading(tenant, null);

        MeterReadingResponse result = mapper.map(reading);

        assertThat(result.meterId()).isNull();
    }

    @Test
    void map_whenReadingIsNull_shouldReturnNull() {
        MeterReadingResponse result = mapper.map((MeterReading) null);

        assertThat(result).isNull();
    }

    @Test
    void map_whenListHasMultipleReadings_shouldMapEach() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        Meter meter = TestEntityFactory.createSavedMeter(tenant, location);
        List<MeterReading> readings = List.of(
                TestEntityFactory.createSavedMeterReading(tenant, meter),
                TestEntityFactory.createSavedMeterReading(tenant, meter));

        List<MeterReadingResponse> result = mapper.map(readings);

        assertThat(result).hasSize(2);
    }

    @Test
    void map_whenListIsEmpty_shouldReturnEmptyList() {
        List<MeterReadingResponse> result = mapper.map(List.of());

        assertThat(result).isEmpty();
    }

    @Test
    void map_whenListIsNull_shouldReturnNull() {
        List<MeterReadingResponse> result = mapper.map((List<MeterReading>) null);

        assertThat(result).isNull();
    }
}
