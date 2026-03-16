package com.github.jknetl.ec.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantAwareJpaRepository<T, ID> extends JpaRepository<T, ID> {

	List<T> findAllByTenantId(String tenantId);
}
