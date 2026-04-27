package com.github.jknetl.ec.service;

import com.github.jknetl.ec.data.model.Meter;
import com.github.jknetl.ec.data.model.Meter;
import com.github.jknetl.ec.data.model.Meter;
import com.github.jknetl.ec.data.repository.LocationRepository;
import com.github.jknetl.ec.data.repository.MeterRepository;
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
public class MeterService {

    private final MeterRepository repository;
    private final LocationRepository locationRepository;

    public Optional<Meter> findById(UUID TenantId, Long id) {
        return repository.findById(id);
    }

    public List<Meter> findAll(UUID TenantId) {
        return repository.findAll();
    }

    @Transactional
    public Meter create(UUID TenantId, Long locationId, Meter meter){
        verifyEntityHasNoId(meter);
        var location = locationRepository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("Location not found: " + locationId));

        meter.setLocation(location);
        return repository.save(meter);
    }

    @Transactional
    public Meter update(UUID TenantId, Long locationId, Meter meter) {
        verifyEntityExists(meter, repository);
        var location = locationRepository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("Location not found: " + locationId));

        meter.setLocation(location);
        return repository.save(meter);
    }

    @Transactional
    public void deleteById(UUID TenantId, Long id) {
        repository.deleteById(id);

    }
}
