package com.github.jknetl.ec.rest;

import com.github.jknetl.ec.rest.dto.MeterReadingRequest;
import com.github.jknetl.ec.rest.dto.MeterReadingResponse;
import com.github.jknetl.ec.rest.mapper.MeterReadingMapper;
import com.github.jknetl.ec.security.AppUserDetails;
import com.github.jknetl.ec.service.MeterReadingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(MeterReadingController.CONTROLLER_PATH)
@RequiredArgsConstructor
public class MeterReadingController {

    public static final String CONTROLLER_PATH = MeterController.CONTROLLER_PATH + "/{meterId}/readings";
    private final MeterReadingService meterReadingService;
    private final MeterReadingMapper mapper;

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<MeterReadingResponse> get(@PathVariable Long meterId,
                                              @PathVariable Long id,
                                              @AuthenticationPrincipal AppUserDetails user) {
        return meterReadingService.findById(user.getTenantId(), id)
                .map(mapper::map);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MeterReadingResponse> getAll(@PathVariable Long meterId,
                                             @AuthenticationPrincipal AppUserDetails user) {
        return mapper.map(meterReadingService.findAll(user.getTenantId(), meterId));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@PathVariable Long meterId,
                    @Valid @RequestBody MeterReadingRequest meterReadingRequest,
                    @AuthenticationPrincipal AppUserDetails user) {
        var meterReading = mapper.map(user.getTenant(), null, meterReadingRequest);
        meterReadingService.create(user.getTenantId(), meterId, meterReading);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void update(@PathVariable Long meterId,
                       @PathVariable Long id,
                       @Valid @RequestBody MeterReadingRequest meterReadingRequest,
                       @AuthenticationPrincipal AppUserDetails user) {
        var meterReading = mapper.map(user.getTenant(), id, meterReadingRequest);
        meterReadingService.update(user.getTenantId(), meterId, meterReading);
    }

    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable Long meterId,
                       @PathVariable Long id,
                       @AuthenticationPrincipal AppUserDetails user) {
        meterReadingService.deleteById(user.getTenantId(), id);
    }
}
