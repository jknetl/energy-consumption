package com.github.jknetl.ec.service;

import java.util.List;
import java.util.Optional;

public interface TenantCrudService<T, ID> {

	T save(String tenantId, T entity);

	Optional<T> findById(String tenantId, ID id);

	List<T> findAll(String tenantId);

	void deleteById(String tenantId, ID id);
}
