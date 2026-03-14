package com.github.jknetl.ec.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.jknetl.ec.data.model.Meter;

public interface MeterRepository extends JpaRepository<Meter, Long> {}
