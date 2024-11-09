package com.HTTTDL.Backend.service;

import com.HTTTDL.Backend.dto.Geo.GeoResponse;
import com.HTTTDL.Backend.mapper.GeoMapper;
import com.HTTTDL.Backend.model.GeoFeature;
import com.HTTTDL.Backend.repository.GeoFeatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FilterService {
    @Autowired
    private GeoFeatureRepository geoFeatureRepository;
    @Autowired
    private GeoMapper geoMapper;

    //tìm theo số sao
    public List<GeoResponse> getGeoFeaturesByStar(int star) {
        List<GeoFeature> geoFeatures = geoFeatureRepository.findByRateGreaterThanEqual(star);
        return geoMapper.toGeoResponse(geoFeatures);
    }
    //tìm theo khoảng cách
    public List<GeoResponse> getGeoFeaturesWithinDistance(double lat, double lon, double distance) {
        List<GeoFeature> geoFeatures =  geoFeatureRepository.findWithinDistance(lat, lon, distance);
        return geoMapper.toGeoResponse(geoFeatures);
    }
}
