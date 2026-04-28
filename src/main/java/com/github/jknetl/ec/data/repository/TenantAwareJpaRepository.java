package com.github.jknetl.ec.data.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface TenantAwareJpaRepository<T, ID> extends JpaRepository<T, ID> {

	List<T> findAllByTenantId(UUID TenantId);
}
