package com.HTTTDL.Backend.service;

import com.HTTTDL.Backend.dto.Filter.FilterByTimeRequest;
import com.HTTTDL.Backend.dto.Geo.GeoResponse;
import com.HTTTDL.Backend.exception.AppException;
import com.HTTTDL.Backend.mapper.GeoMapper;
import com.HTTTDL.Backend.model.GeoFeature;
import com.HTTTDL.Backend.repository.GeoFeatureRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilterService {
    @Autowired
    private GeoFeatureRepository geoFeatureRepository;

    //tìm theo số sao
    public List<GeoResponse> getGeoFeaturesByStar(int star) {
        int end=0;
        if(star == 5){
            end=star;
        }else{
            end=star+1;
        }
        List<GeoFeature> geoFeatures = geoFeatureRepository.findByRateBetween(star, end);
        return geoFeatures.stream()
                .map(this::convertToGeoResponse)
                .collect(Collectors.toList());
    }
    //tìm theo khoảng cách
    public List<GeoResponse> getGeoFeaturesWithinDistance(double lat, double lon, double distance) {
        if (lat < -90 || lat > 90) {
            throw new AppException("Latitude must be within [-90, 90]");
        }
        if (lon < -180 || lon > 180) {
            throw new AppException("Longitude must be within [-180, 180]");
        }
        log.info("Latitude: {}, Longitude: {}, Distance: {}", lat, lon, distance);
        List<GeoFeature> geoFeatures =  geoFeatureRepository.findWithinDistance(lat, lon, distance);
        return geoFeatures.stream()
                .map(this::convertToGeoResponse)
                .collect(Collectors.toList());
    }
    public List<GeoResponse> getGeoFeaturesByTime(FilterByTimeRequest request) {
        List<GeoFeature> geoFeatures = geoFeatureRepository.findOpenGeoFeatures(request.getTime());
        return geoFeatures.stream()
                .map(this::convertToGeoResponse)
                .collect(Collectors.toList());
    }
    private GeoResponse convertToGeoResponse(GeoFeature geoFeature) {
        return GeoResponse.builder()
                .id(geoFeature.getId())
                .name(geoFeature.getName())
                .address(geoFeature.getAddress())
                .phone(geoFeature.getPhone())
                .rate(geoFeature.getRate())
                .open(geoFeature.getOpen())
                .close(geoFeature.getClose())
                .advantage(geoFeature.getAdvantage())
                .disadvantage(geoFeature.getDisadvantage())
                .images(new HashSet<>(geoFeature.getImages())) // chuyển Set<String> thành List<String>
                .build();
    }


}
