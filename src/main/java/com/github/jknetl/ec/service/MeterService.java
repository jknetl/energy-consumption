package com.github.jknetl.ec.service;

import com.github.jknetl.ec.data.model.Meter;
import com.github.jknetl.ec.data.model.Meter;
import com.github.jknetl.ec.data.model.Meter;
import com.github.jknetl.ec.data.repository.MeterRepository;
import com.github.jknetl.ec.data.repository.MeterRepository;
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

    public Optional<Meter> findById(UUID TenantId, Long id) {
        return repository.findById(id);
    }

    public List<Meter> findAll(UUID TenantId) {
        return repository.findAll();
    }

    @Transactional
    public Meter create(UUID TenantId, Meter meter){
        verifyEntityHasNoId(meter);
        return repository.save(meter);
    }

    @Transactional
    public Meter update(UUID TenantId, Meter meter) {
        verifyEntityExists(meter, repository);
        return repository.save(meter);
    }

    @Transactional
    public void deleteById(UUID TenantId, Long id) {
        repository.deleteById(id);

    }
}
