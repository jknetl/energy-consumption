package com.github.jknetl.ec.data.repository;

import org.springframework.stereotype.Repository;

import com.github.jknetl.ec.data.model.MeterReading;

import java.util.List;

@Repository
public interface MeterReadingRepository extends TenantAwareJpaRepository<MeterReading, Long> {

    List<MeterReading> findAllByMeterId(Long meterId);
}
