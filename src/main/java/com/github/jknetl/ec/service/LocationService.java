package com.github.jknetl.ec.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.github.jknetl.ec.data.model.Location;
import com.github.jknetl.ec.data.repository.LocationRepository;
import com.github.jknetl.ec.data.repository.TenantRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.github.jknetl.ec.service.utils.ServiceUtils.verifyEntityHasNoId;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LocationService {

	private final LocationRepository repository;
	private final TenantRepository tenantRepository;

	public Optional<Location> findById(UUID tenantId, Long id) {
		if (id == null) return Optional.empty();
		return repository.findByIdAndTenantId(id, tenantId);
	}

	public List<Location> findAll(UUID tenantId) {
		return repository.findAllByTenantId(tenantId);
	}

	@Transactional
	public Location create(UUID tenantId, Location location) {
		verifyEntityHasNoId(location);
		location.setTenant(tenantRepository.getReferenceById(tenantId));
		return repository.save(location);
	}

	@Transactional
	public Location update(UUID tenantId, Location location) {
		findById(tenantId, location.getId())
				.orElseThrow(() -> new EntityNotFoundException("Location not found: " + location.getId()));
		location.setTenant(tenantRepository.getReferenceById(tenantId));
		return repository.save(location);
	}

	@Transactional
	public void deleteById(UUID tenantId, Long id) {
		findById(tenantId, id)
				.orElseThrow(() -> new EntityNotFoundException("Location not found: " + id));
		repository.deleteById(id);
	}
}
