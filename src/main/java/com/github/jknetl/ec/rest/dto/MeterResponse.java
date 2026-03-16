package com.github.jknetl.ec.rest.dto;

import com.github.jknetl.ec.data.model.EnergyType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record MeterResponse(
        @NotNull
        Long id,
        @NotNull
        EnergyType type,
        @NotNull
        Long locationId
) {
}
