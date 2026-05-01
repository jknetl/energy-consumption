package com.github.jknetl.ec.service;

import com.github.jknetl.ec.data.model.MeterReading;
import com.github.jknetl.ec.data.repository.MeterReadingRepository;
import com.github.jknetl.ec.data.repository.MeterRepository;
import com.github.jknetl.ec.data.repository.TenantRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.github.jknetl.ec.service.utils.ServiceUtils.verifyEntityHasNoId;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MeterReadingService {

	private final MeterReadingRepository repository;
	private final MeterRepository meterRepository;
	private final TenantRepository tenantRepository;

	public Optional<MeterReading> findById(UUID tenantId, Long id) {
		if (id == null) return Optional.empty();
		return repository.findByIdAndTenantId(id, tenantId);
	}

	public List<MeterReading> findAll(UUID tenantId, Long meterId) {
		return repository.findAllByMeterIdAndTenantId(meterId, tenantId);
	}

	@Transactional
	public MeterReading create(UUID tenantId, Long meterId, MeterReading meterReading) {
		verifyEntityHasNoId(meterReading);
		var meter = meterRepository.findByIdAndTenantId(meterId, tenantId)
				.orElseThrow(() -> new EntityNotFoundException("Meter not found: " + meterId));
		meterReading.setMeter(meter);
		meterReading.setTenant(tenantRepository.getReferenceById(tenantId));
		return repository.save(meterReading);
	}

	@Transactional
	public MeterReading update(UUID tenantId, Long meterId, MeterReading meterReading) {
		findById(tenantId, meterReading.getId())
				.orElseThrow(() -> new EntityNotFoundException("MeterReading not found: " + meterReading.getId()));
		var meter = meterRepository.findByIdAndTenantId(meterId, tenantId)
				.orElseThrow(() -> new EntityNotFoundException("Meter not found: " + meterId));
		meterReading.setMeter(meter);
		meterReading.setTenant(tenantRepository.getReferenceById(tenantId));
		return repository.save(meterReading);
	}

	@Transactional
	public void deleteById(UUID tenantId, Long id) {
		findById(tenantId, id)
				.orElseThrow(() -> new EntityNotFoundException("MeterReading not found: " + id));
		repository.deleteById(id);
	}
}
