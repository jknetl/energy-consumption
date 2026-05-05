package com.github.jknetl.ec.data.model;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
public class MeterReading implements TenantScopedEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne(optional = false)
	private Tenant tenant;

	@NotNull
	private BigDecimal value;

	@NotNull
	private EnergyUnit unit;

	@NotNull
	@Column(nullable = false)
	private Instant takenAt;

	@ManyToOne(optional = false)
	private Meter meter;
}
