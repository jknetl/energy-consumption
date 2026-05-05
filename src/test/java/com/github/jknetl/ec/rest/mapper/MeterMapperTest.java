package com.github.jknetl.ec.rest.mapper;

import com.github.jknetl.ec.TestEntityFactory;
import com.github.jknetl.ec.data.model.EnergyType;
import com.github.jknetl.ec.data.model.Location;
import com.github.jknetl.ec.data.model.Meter;
import com.github.jknetl.ec.data.model.Tenant;
import com.github.jknetl.ec.rest.dto.MeterRequest;
import com.github.jknetl.ec.rest.dto.MeterResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MeterMapperTest {

    private final MeterMapper mapper = new MeterMapperImpl();

    @Test
    void map_whenAllArgsProvided_shouldMapTypeNameTenantAndId() {
        Tenant tenant = TestEntityFactory.createTenantA();
        MeterRequest request = MeterRequest.builder()
                .type(EnergyType.GAS)
                .locationId(5L)
                .name("Main Meter")
                .build();

        Meter result = mapper.map(tenant, 10L, request);

        assertThat(result.getTenant()).isEqualTo(tenant);
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getType()).isEqualTo(EnergyType.GAS);
        assertThat(result.getName()).isEqualTo("Main Meter");
    }

    @Test
    void map_whenAllArgsProvided_shouldNotSetLocation() {
        Tenant tenant = TestEntityFactory.createTenantA();
        MeterRequest request = MeterRequest.builder()
                .type(EnergyType.ELECTRICITY)
                .locationId(5L)
                .name("Test Meter")
                .build();

        Meter result = mapper.map(tenant, 1L, request);

        assertThat(result.getLocation()).isNull();
    }

    @Test
    void map_whenMeterHasLocation_shouldExtractLocationId() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        Meter meter = TestEntityFactory.createSavedMeter(tenant, location);

        MeterResponse result = mapper.map(meter);

        assertThat(result.locationId()).isEqualTo(location.getId());
    }

    @Test
    void map_whenMeterLocationIsNull_shouldHaveNullLocationId() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Meter meter = TestEntityFactory.createSavedMeter(tenant, null);

        MeterResponse result = mapper.map(meter);

        assertThat(result.locationId()).isNull();
    }

    @Test
    void map_whenMeterIsNull_shouldReturnNull() {
        MeterResponse result = mapper.map((Meter) null);

        assertThat(result).isNull();
    }

    @Test
    void map_whenListHasMultipleMeters_shouldMapEach() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        List<Meter> meters = List.of(
                TestEntityFactory.createSavedMeter(tenant, location),
                TestEntityFactory.createSavedMeter(tenant, location));

        List<MeterResponse> result = mapper.map(meters);

        assertThat(result).hasSize(2);
    }

    @Test
    void map_whenListIsEmpty_shouldReturnEmptyList() {
        List<MeterResponse> result = mapper.map(List.of());

        assertThat(result).isEmpty();
    }

    @Test
    void map_whenListIsNull_shouldReturnNull() {
        List<MeterResponse> result = mapper.map((List<Meter>) null);

        assertThat(result).isNull();
    }
}
