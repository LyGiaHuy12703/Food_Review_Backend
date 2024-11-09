package com.HTTTDL.Backend.controller;
import com.HTTTDL.Backend.dto.Api.ApiResponse;
import com.HTTTDL.Backend.dto.Geo.GeoRequest;
import com.HTTTDL.Backend.dto.Geo.GeoResponse;
import com.HTTTDL.Backend.service.GeoService;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.geojson.feature.FeatureJSON;
import org.locationtech.jts.io.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/geo")
public class GeoController {
    @Autowired
    private GeoService geoJsonService;

    @GetMapping("/features")
    public ResponseEntity<String> getGeoJsonFeatures() {
        try {
            // Đọc dữ liệu từ file .geojson
            DefaultFeatureCollection features = geoJsonService.readGeoJson("src/main/resources/Data/4.5_week.geojson");

            // Trả về dữ liệu dưới dạng chuỗi JSON
            String jsonOutput = featuresToJson(features);

            return ResponseEntity.ok(jsonOutput);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error reading GeoJSON file");
        }
    }

    // Chuyển đổi FeatureCollection thành chuỗi JSON
    private String featuresToJson(DefaultFeatureCollection features) throws IOException {
        FeatureJSON featureJSON = new FeatureJSON();
        return featureJSON.toString(features);
    }

    //tạo features
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<GeoResponse>> createGeoJson(@ModelAttribute GeoRequest request, @RequestParam("file") List<MultipartFile> files) throws IOException, ParseException {
        ApiResponse<GeoResponse> apiResponse = ApiResponse.<GeoResponse>builder()
                .code("Geo-s-01")
                .message("Create geoFeatures successfully")
                .data(geoJsonService.createGeoFeatures(request, files))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
    //lấy theo id
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GeoResponse>> getGeoFeature(@PathVariable String id) {
        ApiResponse<GeoResponse> apiResponse = ApiResponse.<GeoResponse>builder()
                .code("Geo-s-02")
                .message("Get by geoFeatures id successfully")
                .data(geoJsonService.getGeoFeatureById(id))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
    //lấy hết
    @GetMapping
    public ResponseEntity<ApiResponse<List<GeoResponse>>> getGeoFeatures() {
        ApiResponse<List<GeoResponse>> apiResponse = ApiResponse.<List<GeoResponse>>builder()
                .code("Geo-s-03")
                .message("Get GeoFeatures successfully")
                .data(geoJsonService.getAll())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PutMapping("/{id}")
    public  ResponseEntity<ApiResponse<GeoResponse>> updateGeoFeatures(
            @PathVariable String id,
            @ModelAttribute GeoRequest request,
            @RequestParam("files") List<MultipartFile> files) throws ParseException, IOException {
        ApiResponse<GeoResponse> apiResponse = ApiResponse.<GeoResponse>builder()
                .code("Geo-s-02")
                .message("Update by geoFeatures id successfully")
                .data(geoJsonService.updateFeatures(id, request, files))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGeoFeature(@PathVariable String id) {
        geoJsonService.deleteFeatures(id);
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .code("Geo-s-04")
                .message("Delete GeoFeatures successfully")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

}
