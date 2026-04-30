package com.github.jknetl.ec.service;

import com.github.jknetl.ec.TestEntityFactory;
import com.github.jknetl.ec.data.model.Location;
import com.github.jknetl.ec.data.model.Tenant;
import com.github.jknetl.ec.data.repository.LocationRepository;
import com.github.jknetl.ec.data.repository.TenantRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    private static final UUID TENANT_ID = TestEntityFactory.TENANT_A_ID;

    @Mock private LocationRepository repository;
    @Mock private TenantRepository tenantRepository;
    @InjectMocks private LocationService locationService;

    @Test
    void findById_whenLocationExists_shouldReturnLocation() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        when(repository.findByIdAndTenantId(1L, TENANT_ID)).thenReturn(Optional.of(location));

        Optional<Location> result = locationService.findById(TENANT_ID, 1L);

        assertThat(result).isPresent().contains(location);
    }

    @Test
    void findById_whenLocationDoesNotExist_shouldReturnEmpty() {
        Optional<Location> result = locationService.findById(TENANT_ID, 99L);

        assertThat(result).isEmpty();
    }

    @Test
    void findById_whenIdIsNull_shouldReturnEmpty() {
        Optional<Location> result = locationService.findById(TENANT_ID, null);

        assertThat(result).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllLocations() {
        Tenant tenant = TestEntityFactory.createTenantA();
        List<Location> locations = List.of(
                TestEntityFactory.createSavedLocation(tenant),
                TestEntityFactory.createSavedLocation(tenant));
        when(repository.findAllByTenantId(TENANT_ID)).thenReturn(locations);

        List<Location> result = locationService.findAll(TENANT_ID);

        assertThat(result).hasSize(2);
    }

    @Test
    void create_whenLocationHasNoId_shouldSaveAndReturnLocation() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createLocation(tenant);
        Location saved = TestEntityFactory.createSavedLocation(tenant);
        when(repository.save(location)).thenReturn(saved);

        Location result = locationService.create(TENANT_ID, location);

        assertThat(result).isEqualTo(saved);
        verify(repository).save(location);
    }

    @Test
    void create_shouldSetTenantFromTenantId() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createLocation(null);
        when(tenantRepository.getReferenceById(TENANT_ID)).thenReturn(tenant);
        when(repository.save(any())).thenReturn(TestEntityFactory.createSavedLocation(tenant));

        locationService.create(TENANT_ID, location);

        assertThat(location.getTenant()).isEqualTo(tenant);
    }

    @Test
    void create_whenLocationHasId_shouldThrowRuntimeException() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);

        assertThatThrownBy(() -> locationService.create(TENANT_ID, location))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("When entity is being created then id must not be set.");
        verify(repository, never()).save(any());
    }

    @Test
    void update_shouldSetTenantFromTenantId() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(null);
        when(tenantRepository.getReferenceById(TENANT_ID)).thenReturn(tenant);
        when(repository.findByIdAndTenantId(1L, TENANT_ID)).thenReturn(Optional.of(location));
        when(repository.save(any())).thenReturn(location);

        locationService.update(TENANT_ID, location);

        assertThat(location.getTenant()).isEqualTo(tenant);
    }

    @Test
    void update_whenLocationExists_shouldSaveAndReturnLocation() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        when(repository.findByIdAndTenantId(1L, TENANT_ID)).thenReturn(Optional.of(location));
        when(repository.save(location)).thenReturn(location);

        Location result = locationService.update(TENANT_ID, location);

        assertThat(result).isEqualTo(location);
        verify(repository).save(location);
    }

    @Test
    void update_whenLocationDoesNotExist_shouldThrowEntityNotFoundException() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);

        assertThatThrownBy(() -> locationService.update(TENANT_ID, location))
                .isInstanceOf(EntityNotFoundException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void update_whenLocationIdIsNull_shouldThrowEntityNotFoundException() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createLocation(tenant);

        assertThatThrownBy(() -> locationService.update(TENANT_ID, location))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteById_shouldCallRepositoryDeleteById() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        when(repository.findByIdAndTenantId(1L, TENANT_ID)).thenReturn(Optional.of(location));

        locationService.deleteById(TENANT_ID, 1L);

        verify(repository).deleteById(1L);
    }
}
