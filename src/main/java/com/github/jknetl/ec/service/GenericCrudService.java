package com.github.jknetl.ec.service;

import java.util.List;
import java.util.Optional;

import com.github.jknetl.ec.data.repository.TenantAwareJpaRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class GenericCrudService<T, ID> implements TenantCrudService<T, ID> {

	protected TenantAwareJpaRepository<T, ID> repository;

	@Override
	public T save(String tenantId, T entity) {
		return repository.save(entity);
	}

	@Override
	public Optional<T> findById(String tenantId, ID id) {
		return repository.findById(id);
	}

	@Override
	public List<T> findAll(String tenantId) {
		return repository.findAllByTenantId(tenantId);
	}

	@Override
	public void deleteById(String tenantId, ID id) {
		repository.deleteById(id);

	}
}
