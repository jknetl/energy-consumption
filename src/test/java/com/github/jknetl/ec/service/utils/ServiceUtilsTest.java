package com.github.jknetl.ec.service.utils;

import com.github.jknetl.ec.data.model.Tenant;
import com.github.jknetl.ec.data.model.TenantScopedEntity;
import com.github.jknetl.ec.data.repository.TenantAwareJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceUtilsTest {

    @Mock
    private TenantAwareJpaRepository<FakeEntity, Long> repository;

    @Test
    void verifyEntityHasNoId_whenIdIsNull_shouldNotThrow() {
        var entity = new FakeEntity(null);

        assertThatCode(() -> ServiceUtils.verifyEntityHasNoId(entity)).doesNotThrowAnyException();
    }

    @Test
    void verifyEntityHasNoId_whenIdIsSet_shouldThrowRuntimeException() {
        var entity = new FakeEntity(1L);

        assertThatThrownBy(() -> ServiceUtils.verifyEntityHasNoId(entity))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("When entity is being created then id must not be set.");
    }

    @Test
    void verifyEntityExists_whenEntityIdIsNull_shouldThrowEntityNotFoundException() {
        var entity = new FakeEntity(null);

        assertThatThrownBy(() -> ServiceUtils.verifyEntityExists(entity, repository))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Such entity doesn't exist");
    }

    @Test
    void verifyEntityExists_whenEntityExistsInRepository_shouldNotThrow() {
        var entity = new FakeEntity(1L);
        when(repository.existsById(1L)).thenReturn(true);

        assertThatCode(() -> ServiceUtils.verifyEntityExists(entity, repository)).doesNotThrowAnyException();
    }

    @Test
    void verifyEntityExists_whenEntityNotFoundInRepository_shouldThrowEntityNotFoundException() {
        var entity = new FakeEntity(1L);
        when(repository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> ServiceUtils.verifyEntityExists(entity, repository))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Such entity doesn't exist");
    }

    private record FakeEntity(Long id) implements TenantScopedEntity {
        @Override public Long getId() { return id; }
        @Override public Tenant getTenant() { return null; }
    }
}
