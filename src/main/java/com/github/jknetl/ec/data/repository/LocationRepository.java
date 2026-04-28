package com.github.jknetl.ec.data.repository;

import org.springframework.stereotype.Repository;

import com.github.jknetl.ec.data.model.Location;

import java.util.Optional;

@Repository
public interface LocationRepository extends TenantAwareJpaRepository<Location, Long> {
    Optional<Location> findByStreetAndStreetNumberAndCity(String street, Integer streetNumber, String city);
}
