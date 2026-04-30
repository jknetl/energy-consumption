package com.github.jknetl.ec.service;

import com.github.jknetl.ec.data.model.Meter;
import com.github.jknetl.ec.data.repository.LocationRepository;
import com.github.jknetl.ec.data.repository.MeterRepository;
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
public class MeterService {

    private final MeterRepository repository;
    private final LocationRepository locationRepository;

    public Optional<Meter> findById(UUID tenantId, Long id) {
        if (id == null) return Optional.empty();
        return repository.findByIdAndTenantId(id, tenantId);
    }

    public List<Meter> findAll(UUID tenantId) {
        return repository.findAllByTenantId(tenantId);
    }

    @Transactional
    public Meter create(UUID tenantId, Long locationId, Meter meter){
        verifyEntityHasNoId(meter);
        var location = locationRepository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("Location not found: " + locationId));

        meter.setLocation(location);
        return repository.save(meter);
    }

    @Transactional
    public Meter update(UUID tenantId, Long locationId, Meter meter) {
        findById(tenantId, meter.getId())
                .orElseThrow(() -> new EntityNotFoundException("Meter not found: " + meter.getId()));
        var location = locationRepository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("Location not found: " + locationId));

        meter.setLocation(location);
        return repository.save(meter);
    }

    @Transactional
    public void deleteById(UUID tenantId, Long id) {
        findById(tenantId, id)
                .orElseThrow(() -> new EntityNotFoundException("Meter not found: " + id));
        repository.deleteById(id);
    }
}
