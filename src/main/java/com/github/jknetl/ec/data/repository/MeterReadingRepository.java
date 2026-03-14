package com.github.jknetl.ec.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.jknetl.ec.data.model.MeterReading;

public interface MeterReadingRepository extends JpaRepository<MeterReading, Long> {}
