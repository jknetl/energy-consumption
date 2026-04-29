package com.github.jknetl.ec.service;

import com.github.jknetl.ec.data.model.TenantScopedEntity;
import com.github.jknetl.ec.service.utils.ServiceUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.github.jknetl.ec.data.model.Location;
import com.github.jknetl.ec.data.repository.LocationRepository;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.github.jknetl.ec.service.utils.ServiceUtils.verifyEntityExists;
import static com.github.jknetl.ec.service.utils.ServiceUtils.verifyEntityHasNoId;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LocationService {

	private final LocationRepository repository;

	public Optional<Location> findById(UUID tenantId, Long id) {
		return repository.findById(id)
				.filter(loc -> loc.getTenant().getId().equals(tenantId));
	}

	public List<Location> findAll(UUID tenantId) {
		return repository.findAllByTenantId(tenantId);
	}

	@Transactional
	public Location create(UUID tenantId, Location location){
		verifyEntityHasNoId(location);
		return repository.save(location);
	}

	@Transactional
	public Location update(UUID tenantId, Location location) {
		verifyEntityExists(location, repository);
		return repository.save(location);
	}

	@Transactional
	public void deleteById(UUID tenantId, Long id) {
		repository.deleteById(id);

	}
}
