package com.github.jknetl.ec.rest.mapper;


import com.github.jknetl.ec.data.model.MeterReading;
import com.github.jknetl.ec.data.model.Tenant;
import com.github.jknetl.ec.rest.dto.MeterReadingRequest;
import com.github.jknetl.ec.rest.dto.MeterReadingResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MeterReadingMapper {

    @Mapping(target = "meter", ignore = true)
    MeterReading map(Tenant tenant, Long id, MeterReadingRequest meterReadingRequest);

    @Mapping(target = "meterId", source = "meter.id")
    MeterReadingResponse map(MeterReading meterReading);

    List<MeterReadingResponse> map(List<MeterReading> meterReadings);

}
