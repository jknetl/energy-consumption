package com.github.jknetl.ec.service;

import com.github.jknetl.ec.TestEntityFactory;
import com.github.jknetl.ec.data.model.*;
import com.github.jknetl.ec.data.repository.LocationRepository;
import com.github.jknetl.ec.data.repository.MeterRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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
class MeterServiceTest {

    private static final UUID TENANT_ID = TestEntityFactory.TENANT_A_ID;

    @Mock private MeterRepository repository;
    @Mock private LocationRepository locationRepository;

    @InjectMocks private MeterService meterService;

    @Test
    void findById_whenMeterExists_shouldReturnMeter() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        Meter meter = TestEntityFactory.createSavedMeter(tenant, location);
        when(repository.findByIdAndTenantId(1L, TENANT_ID)).thenReturn(Optional.of(meter));

        Optional<Meter> result = meterService.findById(TENANT_ID, 1L);

        assertThat(result).isPresent().contains(meter);
    }

    @Test
    void findById_whenMeterDoesNotExist_shouldReturnEmpty() {
        assertThat(meterService.findById(TENANT_ID, 99L)).isEmpty();
    }

    @Test
    void findById_whenIdIsNull_shouldReturnEmpty() {
        assertThat(meterService.findById(TENANT_ID, null)).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllMeters() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        List<Meter> meters = List.of(
                TestEntityFactory.createSavedMeter(tenant, location),
                TestEntityFactory.createSavedMeter(tenant, location));
        when(repository.findAllByTenantId(TENANT_ID)).thenReturn(meters);

        assertThat(meterService.findAll(TENANT_ID)).hasSize(2);
    }

    @Test
    void create_whenMeterHasNoId_shouldSaveAndReturnMeter() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        Meter meter = TestEntityFactory.createMeter(tenant, null);
        Meter saved = TestEntityFactory.createSavedMeter(tenant, location);
        when(repository.save(meter)).thenReturn(saved);
        when(locationRepository.findById(location.getId())).thenReturn(Optional.of(location));

        Meter result = meterService.create(TENANT_ID, location.getId(), meter);

        assertThat(result).isEqualTo(saved);
        verify(repository).save(meter);
    }

    @Test
    void create_whenMeterHasId_shouldThrowRuntimeException() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        Meter meter = TestEntityFactory.createSavedMeter(tenant, location);

        assertThatThrownBy(() -> meterService.create(TENANT_ID, location.getId(), meter))
                .isInstanceOf(RuntimeException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void create_whenLocationDoesntExists_shouldThrowRuntimeException() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        Meter meter = TestEntityFactory.createMeter(tenant, null);
        when(locationRepository.findById(location.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> meterService.create(TENANT_ID, location.getId(), meter))
                .isInstanceOf(EntityNotFoundException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void update_whenMeterExists_shouldSaveAndReturnMeter() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        Meter meter = TestEntityFactory.createSavedMeter(tenant, null);
        when(repository.findByIdAndTenantId(1L, TENANT_ID)).thenReturn(Optional.of(meter));
        when(locationRepository.findById(location.getId())).thenReturn(Optional.of(location));
        when(repository.save(meter)).thenReturn(meter);

        Meter result = meterService.update(TENANT_ID, location.getId(), meter);

        assertThat(result).isEqualTo(meter);
        verify(repository).save(meter);
    }

    @Test
    void update_whenLocationDoesntExists_shouldThrowRuntimeException() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        Meter meter = TestEntityFactory.createSavedMeter(tenant, null);
        when(repository.findByIdAndTenantId(1L, TENANT_ID)).thenReturn(Optional.of(meter));
        when(locationRepository.findById(location.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> meterService.update(TENANT_ID, location.getId(), meter))
                .isInstanceOf(EntityNotFoundException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void update_whenMeterDoesNotExist_shouldThrowEntityNotFoundException() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        Meter meter = TestEntityFactory.createSavedMeter(tenant, location);

        assertThatThrownBy(() -> meterService.update(TENANT_ID, location.getId(), meter))
                .isInstanceOf(EntityNotFoundException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void update_whenMeterIdIsNull_shouldThrowEntityNotFoundException() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        Meter meter = TestEntityFactory.createMeter(tenant, location);

        assertThatThrownBy(() -> meterService.update(TENANT_ID, location.getId(), meter))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteById_shouldCallRepositoryDeleteById() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        Meter meter = TestEntityFactory.createSavedMeter(tenant, location);
        when(repository.findByIdAndTenantId(1L, TENANT_ID)).thenReturn(Optional.of(meter));

        meterService.deleteById(TENANT_ID, 1L);

        verify(repository).deleteById(1L);
    }

    @ParameterizedTest
    @EnumSource(EnergyType.class)
    void create_forAllEnergyTypes_shouldSaveAndReturnMeterWithCorrectType(EnergyType type) {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        Meter meter = TestEntityFactory.createMeter(tenant, null);
        meter.setType(type);
        Meter saved = TestEntityFactory.createSavedMeter(tenant, location);
        saved.setType(type);
        when(repository.save(meter)).thenReturn(saved);
        when(locationRepository.findById(location.getId())).thenReturn(Optional.of(location));

        Meter result = meterService.create(TENANT_ID, location.getId(), meter);

        assertThat(result.getType()).isEqualTo(type);
    }
}
