package com.github.jknetl.ec.service;

import com.github.jknetl.ec.TestEntityFactory;
import com.github.jknetl.ec.data.model.Location;
import com.github.jknetl.ec.data.model.Tenant;
import com.github.jknetl.ec.data.repository.LocationRepository;
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
    @InjectMocks private LocationService locationService;

    @Test
    void findById_whenLocationExists_shouldReturnLocation() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        when(repository.findById(1L)).thenReturn(Optional.of(location));

        Optional<Location> result = locationService.findById(TENANT_ID, 1L);

        assertThat(result).isPresent().contains(location);
    }

    @Test
    void findById_whenLocationDoesNotExist_shouldReturnEmpty() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Optional<Location> result = locationService.findById(TENANT_ID, 99L);

        assertThat(result).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllLocations() {
        Tenant tenant = TestEntityFactory.createTenantA();
        List<Location> locations = List.of(
                TestEntityFactory.createSavedLocation(tenant),
                TestEntityFactory.createSavedLocation(tenant));
        when(repository.findAll()).thenReturn(locations);

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
    void create_whenLocationHasId_shouldThrowRuntimeException() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);

        assertThatThrownBy(() -> locationService.create(TENANT_ID, location))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("When entity is being created then id must not be set.");
        verify(repository, never()).save(any());
    }

    @Test
    void update_whenLocationExists_shouldSaveAndReturnLocation() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.save(location)).thenReturn(location);

        Location result = locationService.update(TENANT_ID, location);

        assertThat(result).isEqualTo(location);
        verify(repository).save(location);
    }

    @Test
    void update_whenLocationDoesNotExist_shouldThrowEntityNotFoundException() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        when(repository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> locationService.update(TENANT_ID, location))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Such entity doesn't exist");
        verify(repository, never()).save(any());
    }

    @Test
    void update_whenLocationIdIsNull_shouldThrowEntityNotFoundException() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createLocation(tenant);

        assertThatThrownBy(() -> locationService.update(TENANT_ID, location))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Such entity doesn't exist");
    }

    @Test
    void deleteById_shouldCallRepositoryDeleteById() {
        locationService.deleteById(TENANT_ID, 1L);

        verify(repository).deleteById(1L);
    }
}
