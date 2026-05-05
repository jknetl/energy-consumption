package com.github.jknetl.ec.data.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Entity
public class Meter implements TenantScopedEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne(optional = false)
	private Tenant tenant;

	@Enumerated(EnumType.STRING)
	private EnergyType type;

	@Length(max = 100)
	private String name;

	@ManyToOne(optional = false)
	private Location location;
}
