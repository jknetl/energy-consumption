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
class MeterRepositoryTest {

    @Autowired private TestEntityManager em;
    @Autowired private MeterRepository repository;

    private Tenant tenantA;
    private Tenant tenantB;
    private Location locationA;
    private UUID tenantAId;
    private UUID tenantBId;

    @BeforeEach
    void setUp() {
        tenantA = em.persist(new Tenant(null, "Tenant A"));
        tenantB = em.persist(new Tenant(null, "Tenant B"));
        locationA = em.persist(TestEntityFactory.createLocation(tenantA));
        em.flush();
        tenantAId = em.getId(tenantA, UUID.class);
        tenantBId = em.getId(tenantB, UUID.class);
    }

    @Test
    void findAllByTenantId_whenTenantHasMeters_shouldReturnOnlyTenantMeters() {
        Location locationB = em.persist(TestEntityFactory.createLocation(tenantB));
        em.persist(TestEntityFactory.createMeter(tenantA, locationA));
        em.persist(TestEntityFactory.createMeter(tenantB, locationB));
        em.flush();

        List<Meter> result = repository.findAllByTenantId(tenantAId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTenant()).isEqualTo(tenantA);
    }

    @Test
    void findAllByTenantId_whenTenantHasNoMeters_shouldReturnEmptyList() {
        em.persist(TestEntityFactory.createMeter(tenantA, locationA));
        em.flush();

        List<Meter> result = repository.findAllByTenantId(tenantBId);

        assertThat(result).isEmpty();
    }

    @ParameterizedTest
    @EnumSource(EnergyType.class)
    void save_forAllEnergyTypes_shouldPersistWithCorrectType(EnergyType type) {
        Meter meter = TestEntityFactory.createMeter(tenantA, locationA);
        meter.setType(type);

        Meter saved = repository.save(meter);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getType()).isEqualTo(type);
    }
}
