package com.github.jknetl.ec.data.repository;

import com.github.jknetl.ec.TestEntityFactory;
import com.github.jknetl.ec.data.model.Location;
import com.github.jknetl.ec.data.model.Tenant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LocationRepositoryTest {

    @Autowired private TestEntityManager em;
    @Autowired private LocationRepository repository;

    private Tenant tenantA;
    private Tenant tenantB;
    private UUID tenantAId;
    private UUID tenantBId;

    @BeforeEach
    void setUp() {
        tenantA = em.persist(new Tenant(null, "Tenant A"));
        tenantB = em.persist(new Tenant(null, "Tenant B"));
        em.flush();
        tenantAId = em.getId(tenantA, UUID.class);
        tenantBId = em.getId(tenantB, UUID.class);
    }

    @Test
    void findAllByTenantId_whenTenantHasLocations_shouldReturnOnlyTenantLocations() {
        em.persist(TestEntityFactory.createLocation(tenantA));
        em.persist(TestEntityFactory.createLocation(tenantB));
        em.flush();

        List<Location> result = repository.findAllByTenantId(tenantAId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTenant()).isEqualTo(tenantA);
    }

    @Test
    void findAllByTenantId_whenTenantHasNoLocations_shouldReturnEmptyList() {
        em.persist(TestEntityFactory.createLocation(tenantA));
        em.flush();

        List<Location> result = repository.findAllByTenantId(tenantBId);

        assertThat(result).isEmpty();
    }

    @Test
    void findAllByTenantId_whenNoLocationsExist_shouldReturnEmptyList() {
        List<Location> result = repository.findAllByTenantId(tenantAId);

        assertThat(result).isEmpty();
    }

    @Test
    void save_whenLocationIsValid_shouldPersistWithGeneratedId() {
        Location location = TestEntityFactory.createLocation(tenantA);

        Location saved = repository.save(location);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStreet()).isEqualTo("Main Street");
        assertThat(saved.getCity()).isEqualTo("Prague");
        assertThat(saved.getCountryCode()).isEqualTo("CZE");
    }
}
