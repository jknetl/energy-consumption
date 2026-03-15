package com.github.jknetl.ec.data.model;

import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Tenant {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	@Length(max = 100)
	private String name;
}
