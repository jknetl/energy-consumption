package com.github.jknetl.ec.rest;

import com.github.jknetl.ec.rest.dto.MeterRequest;
import com.github.jknetl.ec.rest.dto.MeterResponse;
import com.github.jknetl.ec.rest.mapper.MeterMapper;
import com.github.jknetl.ec.security.AppUserDetails;
import com.github.jknetl.ec.service.MeterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.github.jknetl.ec.rest.ControllerConstants.API_BASE_PATH;

@RestController
@RequestMapping(MeterController.CONTROLLER_PATH)
@RequiredArgsConstructor
public class MeterController {

    public static final String CONTROLLER_PATH = API_BASE_PATH + "/meters";
    private final MeterService meterService;
    private final MeterMapper mapper;

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<MeterResponse> get(@PathVariable Long id,
                                       @AuthenticationPrincipal AppUserDetails user) {
        return meterService.findById(user.getTenantId(), id)
                .map(mapper::map);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MeterResponse> getAll(@AuthenticationPrincipal AppUserDetails user) {
        var meters = meterService.findAll(user.getTenantId());
        return mapper.map(meters);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@Valid @RequestBody MeterRequest meterRequest,
                    @AuthenticationPrincipal AppUserDetails user) {
        var meter = mapper.map(user.getTenant(), null, meterRequest);
        meterService.create(user.getTenantId(), meterRequest.locationId(), meter);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void update(@PathVariable Long id,
                       @Valid @RequestBody MeterRequest meterRequest,
                       @AuthenticationPrincipal AppUserDetails user) {
        var meter = mapper.map(user.getTenant(), id, meterRequest);
        meterService.update(user.getTenantId(), meterRequest.locationId(), meter);
    }

    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable Long id,
                       @AuthenticationPrincipal AppUserDetails user) {
        meterService.deleteById(user.getTenantId(), id);
    }
}
