package com.HTTTDL.Backend.mapper;

import com.HTTTDL.Backend.dto.Geo.GeoResponse;
import com.HTTTDL.Backend.model.GeoFeature;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GeoMapper {
    List<GeoResponse> toGeoResponse(List<GeoFeature> geoFeatures);
}
