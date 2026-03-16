package com.github.jknetl.ec.service;

import com.github.jknetl.ec.data.model.MeterReading;
import com.github.jknetl.ec.data.repository.MeterReadingRepository;
import com.github.jknetl.ec.data.repository.MeterRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.github.jknetl.ec.service.utils.ServiceUtils.verifyEntityExists;
import static com.github.jknetl.ec.service.utils.ServiceUtils.verifyEntityHasNoId;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MeterReadingService{
	private final MeterReadingRepository repository;
	private final MeterRepository meterRepository;

	public Optional<MeterReading> findById(UUID TenantId, Long id) {
		return repository.findById(id);
	}

	public List<MeterReading> findAll(UUID TenantId, Long meterId) {
		return repository.findAllByMeterId(meterId);
	}

	@Transactional
	public MeterReading create(UUID TenantId, Long meterId, MeterReading meterReading) {
		verifyEntityHasNoId(meterReading);
		var meter = meterRepository.findById(meterId)
				.orElseThrow(() -> new EntityNotFoundException("Meter not found: " + meterId));
		meterReading.setMeter(meter);
		return repository.save(meterReading);
	}

	@Transactional
	public MeterReading update(UUID TenantId, Long meterId, MeterReading meterReading) {
		verifyEntityExists(meterReading, repository);
		var meter = meterRepository.findById(meterId)
				.orElseThrow(() -> new EntityNotFoundException("Meter not found: " + meterId));
		meterReading.setMeter(meter);
		return repository.save(meterReading);
	}

	@Transactional
	public void deleteById(UUID TenantId, Long id) {
		repository.deleteById(id);

	}
}
