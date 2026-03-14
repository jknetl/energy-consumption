package com.github.jknetl.ec.data.model;

import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
public class Location {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@Length(max = 100)
	private String street;

	@NotNull
	private Integer streetNumber;

	@NotNull
	private Integer postalCode;

	@NotNull
	@Length(max = 100)
	private String city;

	@NotNull
	@Length(min = 3, max = 3)
	private String country;
}
