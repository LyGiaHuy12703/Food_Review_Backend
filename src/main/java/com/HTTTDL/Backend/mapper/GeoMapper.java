package com.HTTTDL.Backend.mapper;

import com.HTTTDL.Backend.dto.Geo.GeoResponse;
import com.HTTTDL.Backend.model.GeoFeature;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GeoMapper {
    GeoResponse toGeoResponse(GeoFeature geoFeature);
}
