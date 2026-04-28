package com.github.jknetl.ec.service;

import com.github.jknetl.ec.TestEntityFactory;
import com.github.jknetl.ec.data.model.*;
import com.github.jknetl.ec.data.repository.MeterReadingRepository;
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
class MeterReadingServiceTest {

    private static final UUID TENANT_ID = TestEntityFactory.TENANT_A_ID;
    private static final Long METER_ID = 1L;

    @Mock private MeterReadingRepository repository;
    @Mock private MeterRepository meterRepository;
    @InjectMocks private MeterReadingService meterReadingService;

    @Test
    void findById_whenReadingExists_shouldReturnReading() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        Meter meter = TestEntityFactory.createSavedMeter(tenant, location);
        MeterReading reading = TestEntityFactory.createSavedMeterReading(tenant, meter);
        when(repository.findById(1L)).thenReturn(Optional.of(reading));

        Optional<MeterReading> result = meterReadingService.findById(TENANT_ID, 1L);

        assertThat(result).isPresent().contains(reading);
    }

    @Test
    void findById_whenReadingDoesNotExist_shouldReturnEmpty() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThat(meterReadingService.findById(TENANT_ID, 99L)).isEmpty();
    }

    @Test
    void findAll_whenMeterHasReadings_shouldReturnReadings() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        Meter meter = TestEntityFactory.createSavedMeter(tenant, location);
        List<MeterReading> readings = List.of(TestEntityFactory.createSavedMeterReading(tenant, meter));
        when(repository.findAllByMeterId(METER_ID)).thenReturn(readings);

        assertThat(meterReadingService.findAll(TENANT_ID, METER_ID)).hasSize(1);
    }

    @Test
    void findAll_whenMeterHasNoReadings_shouldReturnEmptyList() {
        when(repository.findAllByMeterId(METER_ID)).thenReturn(List.of());

        assertThat(meterReadingService.findAll(TENANT_ID, METER_ID)).isEmpty();
    }

    @Test
    void create_whenValidReadingAndMeterExists_shouldSetMeterAndSaveReading() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        Meter meter = TestEntityFactory.createSavedMeter(tenant, location);
        MeterReading reading = TestEntityFactory.createMeterReading(tenant, null);
        MeterReading saved = TestEntityFactory.createSavedMeterReading(tenant, meter);
        when(meterRepository.findById(METER_ID)).thenReturn(Optional.of(meter));
        when(repository.save(reading)).thenReturn(saved);

        MeterReading result = meterReadingService.create(TENANT_ID, METER_ID, reading);

        assertThat(reading.getMeter()).isEqualTo(meter);
        assertThat(result).isEqualTo(saved);
        verify(repository).save(reading);
    }

    @Test
    void create_whenReadingHasId_shouldThrowRuntimeExceptionBeforeCallingMeterRepository() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        Meter meter = TestEntityFactory.createSavedMeter(tenant, location);
        MeterReading reading = TestEntityFactory.createSavedMeterReading(tenant, meter);

        assertThatThrownBy(() -> meterReadingService.create(TENANT_ID, METER_ID, reading))
                .isInstanceOf(RuntimeException.class);
        verify(meterRepository, never()).findById(any());
        verify(repository, never()).save(any());
    }

    @Test
    void create_whenMeterDoesNotExist_shouldThrowEntityNotFoundException() {
        Tenant tenant = TestEntityFactory.createTenantA();
        MeterReading reading = TestEntityFactory.createMeterReading(tenant, null);
        when(meterRepository.findById(METER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> meterReadingService.create(TENANT_ID, METER_ID, reading))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Meter not found");
        verify(repository, never()).save(any());
    }

    @Test
    void update_whenReadingExistsAndMeterExists_shouldSetMeterAndSaveReading() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        Meter meter = TestEntityFactory.createSavedMeter(tenant, location);
        MeterReading reading = TestEntityFactory.createSavedMeterReading(tenant, meter);
        when(repository.existsById(1L)).thenReturn(true);
        when(meterRepository.findById(METER_ID)).thenReturn(Optional.of(meter));
        when(repository.save(reading)).thenReturn(reading);

        MeterReading result = meterReadingService.update(TENANT_ID, METER_ID, reading);

        assertThat(result).isEqualTo(reading);
        verify(repository).save(reading);
    }

    @Test
    void update_whenReadingIdIsNull_shouldThrowEntityNotFoundException() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        Meter meter = TestEntityFactory.createSavedMeter(tenant, location);
        MeterReading reading = TestEntityFactory.createMeterReading(tenant, meter);

        assertThatThrownBy(() -> meterReadingService.update(TENANT_ID, METER_ID, reading))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void update_whenReadingDoesNotExist_shouldThrowEntityNotFoundExceptionBeforeCallingMeterRepository() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        Meter meter = TestEntityFactory.createSavedMeter(tenant, location);
        MeterReading reading = TestEntityFactory.createSavedMeterReading(tenant, meter);
        when(repository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> meterReadingService.update(TENANT_ID, METER_ID, reading))
                .isInstanceOf(EntityNotFoundException.class);
        verify(meterRepository, never()).findById(any());
        verify(repository, never()).save(any());
    }

    @Test
    void update_whenMeterDoesNotExist_shouldThrowEntityNotFoundException() {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        Meter meter = TestEntityFactory.createSavedMeter(tenant, location);
        MeterReading reading = TestEntityFactory.createSavedMeterReading(tenant, meter);
        when(repository.existsById(1L)).thenReturn(true);
        when(meterRepository.findById(METER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> meterReadingService.update(TENANT_ID, METER_ID, reading))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Meter not found");
        verify(repository, never()).save(any());
    }

    @Test
    void deleteById_shouldCallRepositoryDeleteById() {
        meterReadingService.deleteById(TENANT_ID, 1L);

        verify(repository).deleteById(1L);
    }

    @ParameterizedTest
    @EnumSource(EnergyUnit.class)
    void create_forAllEnergyUnits_shouldSaveReadingWithCorrectUnit(EnergyUnit unit) {
        Tenant tenant = TestEntityFactory.createTenantA();
        Location location = TestEntityFactory.createSavedLocation(tenant);
        Meter meter = TestEntityFactory.createSavedMeter(tenant, location);
        MeterReading reading = TestEntityFactory.createMeterReading(tenant, null);
        reading.setUnit(unit);
        MeterReading saved = TestEntityFactory.createSavedMeterReading(tenant, meter);
        saved.setUnit(unit);
        when(meterRepository.findById(METER_ID)).thenReturn(Optional.of(meter));
        when(repository.save(reading)).thenReturn(saved);

        MeterReading result = meterReadingService.create(TENANT_ID, METER_ID, reading);

        assertThat(result.getUnit()).isEqualTo(unit);
    }
}
