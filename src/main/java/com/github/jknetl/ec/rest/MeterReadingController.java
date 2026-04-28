package com.github.jknetl.ec.rest;

import com.github.jknetl.ec.data.model.Tenant;
import com.github.jknetl.ec.rest.dto.MeterReadingRequest;
import com.github.jknetl.ec.rest.dto.MeterReadingResponse;
import com.github.jknetl.ec.rest.mapper.MeterReadingMapper;
import com.github.jknetl.ec.service.MeterReadingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.github.jknetl.ec.data.model.Tenant.UNIMPLEMENTED_TENANT_ID;

@RestController
@RequestMapping(MeterReadingController.CONTROLLER_PATH)
@RequiredArgsConstructor
public class MeterReadingController {

    public static final String CONTROLLER_PATH = MeterController.CONTROLLER_PATH + "/{meterId}/readings";
    private final MeterReadingService meterReadingService;
    private final MeterReadingMapper mapper;

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<MeterReadingResponse> get(@PathVariable Long meterId, @PathVariable Long id) {
        return meterReadingService.findById(UNIMPLEMENTED_TENANT_ID, id)
                .map(mapper::map);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MeterReadingResponse> getAll(@PathVariable Long meterId) {
        return mapper.map(meterReadingService.findAll(UNIMPLEMENTED_TENANT_ID, meterId));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@PathVariable Long meterId, @Valid @RequestBody MeterReadingRequest meterReadingRequest) {
        var meterReading = mapper.map(Tenant.UNIMPLEMENTED_TENANCY_TENANT, null, meterReadingRequest);
        meterReadingService.create(UNIMPLEMENTED_TENANT_ID, meterId, meterReading);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void update(@PathVariable Long meterId, @PathVariable Long id, @Valid @RequestBody MeterReadingRequest meterReadingRequest) {
        var meterReading = mapper.map(Tenant.UNIMPLEMENTED_TENANCY_TENANT, id, meterReadingRequest);
        meterReadingService.update(UNIMPLEMENTED_TENANT_ID, meterId, meterReading);
    }

    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable Long meterId, @PathVariable Long id) {
        meterReadingService.deleteById(UNIMPLEMENTED_TENANT_ID, id);
    }
}
