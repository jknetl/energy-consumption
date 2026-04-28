package com.github.jknetl.ec.rest.mapper;

import com.github.jknetl.ec.TestEntityFactory;
import com.github.jknetl.ec.data.model.Location;
import com.github.jknetl.ec.data.model.Tenant;
import com.github.jknetl.ec.rest.dto.LocationRequest;
import com.github.jknetl.ec.rest.dto.LocationResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LocationMapperTest {

    private final LocationMapper mapper = new LocationMapperImpl();

    @Test
    void map_whenAllArgsProvided_shouldMapAllFields() {
        Tenant tenant = TestEntityFactory.createTenantA();
        LocationRequest request = LocationRequest.builder()
                .street("Main Street")
                .streetNumber(1)
                .postalCode(10000)
                .city("Prague")
                .countryCode("CZE")
                .build();

        Location result = mapper.map(tenant, 42L, request);

        assertThat(result.getTenant()).isEqualTo(tenant);
        assertThat(result.getId()).isEqualTo(42L);
        assertThat(result.getStreet()).isEqualTo("Main Street");
        assertThat(result.getStreetNumber()).isEqualTo(1);
        assertThat(result.getPostalCode()).isEqualTo(10000);
        assertThat(result.getCity()).isEqualTo("Prague");
        assertThat(result.getCountryCode()).isEqualTo("CZE");
    }

    @Test
    void map_whenRequestIsNull_shouldSetOnlyTenantAndId() {
        Tenant tenant = TestEntityFactory.createTenantA();

        Location result = mapper.map(tenant, 7L, null);

        assertThat(result.getTenant()).isEqualTo(tenant);
        assertThat(result.getId()).isEqualTo(7L);
        assertThat(result.getStreet()).isNull();
        assertThat(result.getCity()).isNull();
        assertThat(result.getCountryCode()).isNull();
    }

    @Test
    void map_whenAllArgsNull_shouldReturnNull() {
        Location result = mapper.map((Tenant) null, null, (LocationRequest) null);

        assertThat(result).isNull();
    }

    @Test
    void map_whenLocationHasAllFields_shouldMapAllFields() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);

        LocationResponse result = mapper.map(location);

        assertThat(result.id()).isEqualTo(location.getId());
        assertThat(result.street()).isEqualTo(location.getStreet());
        assertThat(result.streetNumber()).isEqualTo(location.getStreetNumber());
        assertThat(result.postalCode()).isEqualTo(location.getPostalCode());
        assertThat(result.city()).isEqualTo(location.getCity());
        assertThat(result.countryCode()).isEqualTo(location.getCountryCode());
    }

    @Test
    void map_whenLocationIsNull_shouldReturnNull() {
        LocationResponse result = mapper.map((Location) null);

        assertThat(result).isNull();
    }

    @Test
    void map_whenListHasMultipleLocations_shouldMapEach() {
        Tenant tenant = TestEntityFactory.createTenantA();
        List<Location> locations = List.of(
                TestEntityFactory.createSavedLocation(tenant),
                TestEntityFactory.createSavedLocation(tenant));

        List<LocationResponse> result = mapper.map(locations);

        assertThat(result).hasSize(2);
    }

    @Test
    void map_whenListIsEmpty_shouldReturnEmptyList() {
        List<LocationResponse> result = mapper.map(List.of());

        assertThat(result).isEmpty();
    }

    @Test
    void map_whenListIsNull_shouldReturnNull() {
        List<LocationResponse> result = mapper.map((List<Location>) null);

        assertThat(result).isNull();
    }
}
