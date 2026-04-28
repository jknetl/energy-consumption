package com.github.jknetl.ec.rest.mapper;


import com.github.jknetl.ec.data.model.Meter;
import com.github.jknetl.ec.data.model.Tenant;
import com.github.jknetl.ec.rest.dto.MeterRequest;
import com.github.jknetl.ec.rest.dto.MeterResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MeterMapper {

    Meter map(Tenant tenant, Long id, MeterRequest meterRequest);

    @Mapping(target = "locationId", source = "location.id")
    MeterResponse map(Meter meter);

    List<MeterResponse> map(List<Meter> meters);

}
