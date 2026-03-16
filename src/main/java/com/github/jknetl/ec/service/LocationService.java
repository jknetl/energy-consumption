package com.github.jknetl.ec.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.github.jknetl.ec.data.model.Location;
import com.github.jknetl.ec.data.repository.LocationRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class LocationService implements TenantCrudService<Location, Long> {

	private final LocationRepository locationRepository;

	@Override
	public Location save(Location location) {
		return locationRepository.save(location);
	}

	@Override
	public Optional<Location> findById(Long id) {
		return locationRepository.findById(id);
	}

	@Override
	public List<Location> findAll() {
		return locationRepository.findAll();
	}

	@Override
	public List<Location> findAllByTenantId(String tenantId) {
		return locationRepository.findAllByTenantId(tenantId);
	}

	@Override
	public void deleteById(Long id) {
		locationRepository.deleteById(id);
	}
}
