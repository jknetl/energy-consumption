package com.github.jknetl.ec.service.utils;

import com.github.jknetl.ec.data.model.TenantScopedEntity;
import com.github.jknetl.ec.data.repository.TenantAwareJpaRepository;
import jakarta.persistence.EntityNotFoundException;

public final class ServiceUtils {

    private ServiceUtils() {};

    public static void verifyEntityHasNoId(TenantScopedEntity entity) {
        if (entity.getId() != null) {
            throw new RuntimeException("When entity is being created then id must not be set.");
        }
    }

    public static void verifyEntityExists(TenantScopedEntity entity, TenantAwareJpaRepository<?, Long> repository) {
        if (entity.getId() == null || !repository.existsById(entity.getId())) {
            throw new EntityNotFoundException("Such entity doesn't exist");
        }
    }
}
