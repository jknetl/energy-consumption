package com.github.jknetl.ec.rest;

import com.github.jknetl.ec.rest.dto.LocationRequest;
import com.github.jknetl.ec.rest.dto.LocationResponse;
import com.github.jknetl.ec.rest.mapper.LocationMapper;
import com.github.jknetl.ec.security.AppUserDetails;
import com.github.jknetl.ec.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(LocationController.CONTROLLER_PATH)
@RequiredArgsConstructor
public class LocationController {

    public static final String CONTROLLER_PATH = ControllerConstants.API_BASE_PATH + "/locations";

    private final LocationService locationService;
    private final LocationMapper mapper;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<LocationResponse> getAll(@AuthenticationPrincipal AppUserDetails user) {
        return mapper.map(locationService.findAll(user.getTenantId()));
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<LocationResponse> get(@PathVariable Long id,
                                          @AuthenticationPrincipal AppUserDetails user) {
        return locationService.findById(user.getTenantId(), id).map(mapper::map);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@Valid @RequestBody LocationRequest locationRequest,
                    @AuthenticationPrincipal AppUserDetails user) {
        var location = mapper.map(user.getTenant(), null, locationRequest);
        locationService.create(user.getTenantId(), location);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void update(@PathVariable Long id,
                       @Valid @RequestBody LocationRequest locationRequest,
                       @AuthenticationPrincipal AppUserDetails user) {
        var location = mapper.map(user.getTenant(), id, locationRequest);
        locationService.update(user.getTenantId(), location);
    }

    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable Long id,
                       @AuthenticationPrincipal AppUserDetails user) {
        locationService.deleteById(user.getTenantId(), id);
    }
}
