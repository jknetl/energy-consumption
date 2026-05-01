package com.github.jknetl.ec.data.repository;

import org.springframework.stereotype.Repository;

import com.github.jknetl.ec.data.model.MeterReading;

import java.util.List;
import java.util.UUID;

@Repository
public interface MeterReadingRepository extends TenantAwareJpaRepository<MeterReading, Long> {

    List<MeterReading> findAllByMeterId(Long meterId);

    List<MeterReading> findAllByMeterIdAndTenantId(Long meterId, UUID tenantId);
}
