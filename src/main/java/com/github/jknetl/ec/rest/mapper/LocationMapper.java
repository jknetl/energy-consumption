package com.github.jknetl.ec.rest.mapper;

import com.github.jknetl.ec.data.model.Location;
import com.github.jknetl.ec.data.model.Tenant;
import com.github.jknetl.ec.rest.dto.LocationRequest;
import com.github.jknetl.ec.rest.dto.LocationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LocationMapper {

    Location map(Tenant tenant, Long id, LocationRequest locationRequest);

    LocationResponse map(Location location);

    List<LocationResponse> map(List<Location> locations);
}
