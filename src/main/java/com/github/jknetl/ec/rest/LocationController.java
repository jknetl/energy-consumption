package com.github.jknetl.ec.rest;

import java.util.List;
import java.util.Optional;

import com.github.jknetl.ec.data.model.Tenant;
import com.github.jknetl.ec.rest.dto.LocationRequest;
import com.github.jknetl.ec.rest.dto.LocationResponse;
import com.github.jknetl.ec.rest.mapper.LocationMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.github.jknetl.ec.service.LocationService;

import lombok.RequiredArgsConstructor;

import static com.github.jknetl.ec.data.model.Tenant.UNIMPLEMENTED_TENANT_ID;

@RestController
@RequestMapping(LocationController.CONTROLLER_PATH)
@RequiredArgsConstructor
public class LocationController {

	public static final String CONTROLLER_PATH = ControllerConstants.API_BASE_PATH + "/locations";

	private final LocationService locationService;
	private final LocationMapper mapper;

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public List<LocationResponse> getAll() {
		var locations = locationService.findAll(UNIMPLEMENTED_TENANT_ID);
		return mapper.map(locations);

	}

	@GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Optional<LocationResponse> get(@PathVariable Long id) {
		return locationService.findById(UNIMPLEMENTED_TENANT_ID, id)
				.map(mapper::map);
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public void add(@Valid @RequestBody LocationRequest locationRequest) {
		var location = mapper.map(Tenant.UNIMPLEMENTED_TENANCY_TENANT, null, locationRequest);
		locationService.create(UNIMPLEMENTED_TENANT_ID, location);
	}

	@PutMapping( path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void update(@PathVariable Long id, @Valid @RequestBody LocationRequest locationRequest) {
		var location = mapper.map(Tenant.UNIMPLEMENTED_TENANCY_TENANT, id,  locationRequest);
		locationService.update(UNIMPLEMENTED_TENANT_ID, location);
	}

	@DeleteMapping(path = "/{id}")
	public void delete(@PathVariable Long id) {
		locationService.deleteById(UNIMPLEMENTED_TENANT_ID, id);
	}
}
