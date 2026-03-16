package com.github.jknetl.ec.rest.dto;

import com.github.jknetl.ec.data.model.EnergyUnit;
import com.github.jknetl.ec.data.model.Meter;
import com.github.jknetl.ec.data.model.Tenant;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record MeterReadingRequest(
        @NotNull
        @Min(0)
        BigDecimal value,
        @NotNull
        EnergyUnit unit
) {
}
