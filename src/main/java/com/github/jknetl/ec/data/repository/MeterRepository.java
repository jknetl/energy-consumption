package com.github.jknetl.ec.data.repository;

import org.springframework.stereotype.Repository;

import com.github.jknetl.ec.data.model.Meter;
import com.github.jknetl.ec.data.model.Location;
import com.github.jknetl.ec.data.model.EnergyType;

import java.util.Optional;

@Repository
public interface MeterRepository extends TenantAwareJpaRepository<Meter, Long> {
    Optional<Meter> findByLocationAndType(Location location, EnergyType type);
}
