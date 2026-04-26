package com.github.jknetl.ec.data.repository;

import com.github.jknetl.ec.TestEntityFactory;
import com.github.jknetl.ec.data.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MeterReadingRepositoryTest {

    @Autowired private TestEntityManager em;
    @Autowired private MeterReadingRepository repository;

    private Tenant tenantA;
    private Tenant tenantB;
    private Meter meterA;
    private Meter meterB;
    private UUID tenantAId;
    private UUID tenantBId;

    @BeforeEach
    void setUp() {
        tenantA = em.persist(new Tenant(null, "Tenant A"));
        tenantB = em.persist(new Tenant(null, "Tenant B"));
        Location locationA = em.persist(TestEntityFactory.createLocation(tenantA));
        Location locationB = em.persist(TestEntityFactory.createLocation(tenantB));
        meterA = em.persist(TestEntityFactory.createMeter(tenantA, locationA));
        meterB = em.persist(TestEntityFactory.createMeter(tenantB, locationB));
        em.flush();
        tenantAId = em.getId(tenantA, UUID.class);
        tenantBId = em.getId(tenantB, UUID.class);
    }

    @Test
    void findAllByTenantId_whenTenantHasReadings_shouldReturnOnlyTenantReadings() {
        em.persist(TestEntityFactory.createMeterReading(tenantA, meterA));
        em.persist(TestEntityFactory.createMeterReading(tenantB, meterB));
        em.flush();

        List<MeterReading> result = repository.findAllByTenantId(tenantAId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTenant()).isEqualTo(tenantA);
    }

    @Test
    void findAllByTenantId_whenTenantHasNoReadings_shouldReturnEmptyList() {
        em.persist(TestEntityFactory.createMeterReading(tenantA, meterA));
        em.flush();

        List<MeterReading> result = repository.findAllByTenantId(tenantBId);

        assertThat(result).isEmpty();
    }

    @Test
    void findAllByMeterId_whenMeterHasReadings_shouldReturnAllReadingsForThatMeter() {
        em.persist(TestEntityFactory.createMeterReading(tenantA, meterA));
        em.persist(TestEntityFactory.createMeterReading(tenantA, meterA));
        em.persist(TestEntityFactory.createMeterReading(tenantB, meterB));
        em.flush();

        List<MeterReading> result = repository.findAllByMeterId(meterA.getId());

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(r -> r.getMeter().getId().equals(meterA.getId()));
    }

    @Test
    void findAllByMeterId_whenMeterHasNoReadings_shouldReturnEmptyList() {
        em.persist(TestEntityFactory.createMeterReading(tenantB, meterB));
        em.flush();

        List<MeterReading> result = repository.findAllByMeterId(meterA.getId());

        assertThat(result).isEmpty();
    }

    @ParameterizedTest
    @EnumSource(EnergyUnit.class)
    void save_forAllEnergyUnits_shouldPersistWithCorrectUnit(EnergyUnit unit) {
        MeterReading reading = TestEntityFactory.createMeterReading(tenantA, meterA);
        reading.setUnit(unit);

        MeterReading saved = repository.save(reading);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUnit()).isEqualTo(unit);
    }
}
