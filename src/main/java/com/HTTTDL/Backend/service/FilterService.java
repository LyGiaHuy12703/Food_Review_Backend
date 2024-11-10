package com.HTTTDL.Backend.service;

import com.HTTTDL.Backend.dto.Geo.GeoResponse;
import com.HTTTDL.Backend.mapper.GeoMapper;
import com.HTTTDL.Backend.model.GeoFeature;
import com.HTTTDL.Backend.repository.GeoFeatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilterService {
    @Autowired
    private GeoFeatureRepository geoFeatureRepository;
    @Autowired
    private GeoMapper geoMapper;

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
        List<GeoFeature> geoFeatures =  geoFeatureRepository.findWithinDistance(lat, lon, distance);
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
