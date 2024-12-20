package com.HTTTDL.Backend.controller;

import com.HTTTDL.Backend.dto.Api.ApiResponse;
import com.HTTTDL.Backend.dto.Filter.FilterByTimeRequest;
import com.HTTTDL.Backend.dto.Geo.GeoResponse;
import com.HTTTDL.Backend.service.FilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/filter")
public class FilterController {
    @Autowired
    private FilterService filterService;

    @GetMapping("/by-star")
    public ResponseEntity<ApiResponse<List<GeoResponse>>> getGeoFeaturesByStar(@RequestParam int star) {
        ApiResponse<List<GeoResponse>> apiResponse = ApiResponse.<List<GeoResponse>>builder()
                .message("get all by star success")
                .data(filterService.getGeoFeaturesByStar(star))
                .code("filter-s-01")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
    @GetMapping("/by-distance")
    public ResponseEntity<ApiResponse<List<GeoResponse>>> getGeoFeaturesWithinDistance(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam double distance) { // distance gửi đơn vị mét
        double transferDistance = distance * 1000;
        ApiResponse<List<GeoResponse>> apiResponse = ApiResponse.<List<GeoResponse>>builder()
                .message("get all by distance success")
                .data(filterService.getGeoFeaturesWithinDistance(lat, lon, transferDistance))
                .code("filter-s-02")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping("/by-time")
    public ResponseEntity<ApiResponse<List<GeoResponse>>> getGeoFeaturesByTime(
            @RequestBody FilterByTimeRequest request
    ){
        ApiResponse<List<GeoResponse>> apiResponse = ApiResponse.<List<GeoResponse>>builder()
                .message("get all by star success")
                .data(filterService.getGeoFeaturesByTime(request))
                .code("filter-s-01")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<GeoResponse>>> getGeoFeatures(
            @RequestParam String name
    ){
        ApiResponse<List<GeoResponse>> apiResponse = ApiResponse.<List<GeoResponse>>builder()
                .message("search geo success")
                .code("filter-s-04")
                .data(filterService.getGeoFeaturesBySearch(name))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
