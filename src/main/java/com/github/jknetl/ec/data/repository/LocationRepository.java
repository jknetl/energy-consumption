package com.github.jknetl.ec.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.jknetl.ec.data.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {}
