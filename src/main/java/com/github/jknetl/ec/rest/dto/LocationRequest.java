package com.github.jknetl.ec.rest.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record LocationRequest(
        @NotNull
        @Length(max = 100)
        String street,
        int streetNumber,
        int postalCode,
        @NotNull
        @Length(max = 100)
        String city,
        @NotNull
        @Length(min =3, max=3)
        String countryCode
) {}
