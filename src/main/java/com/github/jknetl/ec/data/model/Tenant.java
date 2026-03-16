package com.github.jknetl.ec.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Tenant {

	//TODO: remove this once authentication is done and tenant is inferred from user
	public static UUID UNIMPLEMENTED_TENANT_ID = UUID.fromString("ec407ae7-bbfb-4d2a-a788-517be9e5b13c");
	public static Tenant UNIMPLEMENTED_TENANCY_TENANT = new Tenant(UNIMPLEMENTED_TENANT_ID, "UNIMPLEMENTED");
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Length(max = 100)
	private String name;
}
