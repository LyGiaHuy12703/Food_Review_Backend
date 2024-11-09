package com.HTTTDL.Backend.controller;

import com.HTTTDL.Backend.dto.Api.ApiResponse;
import com.HTTTDL.Backend.dto.Geo.GeoResponse;
import com.HTTTDL.Backend.service.FilterService;
import com.HTTTDL.Backend.service.GeoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        ApiResponse<List<GeoResponse>> apiResponse = ApiResponse.<List<GeoResponse>>builder()
                .message("get all by distance success")
                .data(filterService.getGeoFeaturesWithinDistance(lat, lon, distance))
                .code("filter-s-02")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
