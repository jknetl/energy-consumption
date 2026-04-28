package com.github.jknetl.ec.rest.dto;

import com.github.jknetl.ec.data.model.EnergyType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record MeterRequest(
        @NotNull
        EnergyType type,
        @NotNull
        Long locationId
) {
}
