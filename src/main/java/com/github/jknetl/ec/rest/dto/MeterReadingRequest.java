package com.github.jknetl.ec.rest.dto;

import com.github.jknetl.ec.data.model.EnergyUnit;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record MeterReadingRequest(
        @NotNull
        @Min(0)
        BigDecimal value,
        @NotNull
        EnergyUnit unit,
        @NotNull
        Instant takenAt
) {
}
