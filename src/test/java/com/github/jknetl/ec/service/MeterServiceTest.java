package com.github.jknetl.ec.service;

import com.github.jknetl.ec.TestEntityFactory;
import com.github.jknetl.ec.data.model.*;
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
    @InjectMocks private MeterService meterService;

    @Test
    void findById_whenMeterExists_shouldReturnMeter() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        Meter meter = TestEntityFactory.createSavedMeter(tenant, location);
        when(repository.findById(1L)).thenReturn(Optional.of(meter));

        Optional<Meter> result = meterService.findById(TENANT_ID, 1L);

        assertThat(result).isPresent().contains(meter);
    }

    @Test
    void findById_whenMeterDoesNotExist_shouldReturnEmpty() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThat(meterService.findById(TENANT_ID, 99L)).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllMeters() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        List<Meter> meters = List.of(
                TestEntityFactory.createSavedMeter(tenant, location),
                TestEntityFactory.createSavedMeter(tenant, location));
        when(repository.findAll()).thenReturn(meters);

        assertThat(meterService.findAll(TENANT_ID)).hasSize(2);
    }

    @Test
    void create_whenMeterHasNoId_shouldSaveAndReturnMeter() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        Meter meter = TestEntityFactory.createMeter(tenant, location);
        Meter saved = TestEntityFactory.createSavedMeter(tenant, location);
        when(repository.save(meter)).thenReturn(saved);

        Meter result = meterService.create(TENANT_ID, meter);

        assertThat(result).isEqualTo(saved);
        verify(repository).save(meter);
    }

    @Test
    void create_whenMeterHasId_shouldThrowRuntimeException() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        Meter meter = TestEntityFactory.createSavedMeter(tenant, location);

        assertThatThrownBy(() -> meterService.create(TENANT_ID, meter))
                .isInstanceOf(RuntimeException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void update_whenMeterExists_shouldSaveAndReturnMeter() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        Meter meter = TestEntityFactory.createSavedMeter(tenant, location);
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.save(meter)).thenReturn(meter);

        Meter result = meterService.update(TENANT_ID, meter);

        assertThat(result).isEqualTo(meter);
        verify(repository).save(meter);
    }

    @Test
    void update_whenMeterDoesNotExist_shouldThrowEntityNotFoundException() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        Meter meter = TestEntityFactory.createSavedMeter(tenant, location);
        when(repository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> meterService.update(TENANT_ID, meter))
                .isInstanceOf(EntityNotFoundException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void update_whenMeterIdIsNull_shouldThrowEntityNotFoundException() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        Meter meter = TestEntityFactory.createMeter(tenant, location);

        assertThatThrownBy(() -> meterService.update(TENANT_ID, meter))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteById_shouldCallRepositoryDeleteById() {
        meterService.deleteById(TENANT_ID, 1L);

        verify(repository).deleteById(1L);
    }

    @ParameterizedTest
    @EnumSource(EnergyType.class)
    void create_forAllEnergyTypes_shouldSaveAndReturnMeterWithCorrectType(EnergyType type) {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        Meter meter = TestEntityFactory.createMeter(tenant, location);
        meter.setType(type);
        Meter saved = TestEntityFactory.createSavedMeter(tenant, location);
        saved.setType(type);
        when(repository.save(meter)).thenReturn(saved);

        Meter result = meterService.create(TENANT_ID, meter);

        assertThat(result.getType()).isEqualTo(type);
    }
}
