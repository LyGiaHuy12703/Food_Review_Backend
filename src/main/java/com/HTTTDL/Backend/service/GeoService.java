package com.HTTTDL.Backend.service;
import com.HTTTDL.Backend.dto.Geo.GeoRequest;
import com.HTTTDL.Backend.dto.Geo.GeoResponse;
import com.HTTTDL.Backend.exception.AppException;
import com.HTTTDL.Backend.model.GeoFeature;
import com.HTTTDL.Backend.repository.GeoFeatureRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.geojson.feature.FeatureJSON;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GeoService {
    @Autowired
    private GeoFeatureRepository geoFeatureRepository;
    @Autowired
    private CloudinaryService cloudinaryService;

    public DefaultFeatureCollection readGeoJson(String filePath) throws IOException {
        File geoJsonFile = new File(filePath);

        // Sử dụng GeoTools FeatureJSON để đọc dữ liệu từ file .geojson
        FeatureJSON featureJSON = new FeatureJSON();

        // Tạo đối tượng DefaultFeatureCollection để lưu các feature đọc được
        DefaultFeatureCollection featureCollection = new DefaultFeatureCollection();

        // Đọc dữ liệu từ file geojson
        try (FileReader reader = new FileReader(geoJsonFile)) {
            featureCollection.addAll(featureJSON.readFeatureCollection(reader));
        }

        return featureCollection;
    }
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public GeoResponse createGeoFeatures(GeoRequest request, List<MultipartFile> files) throws IOException {
        boolean checkExist = geoFeatureRepository.existsByName(request.getName());
        if(checkExist){
            throw new AppException(HttpStatus.BAD_REQUEST, "GeoFeature already exists");
        }

        Point point = createPointFromCoordinates(request.getLat(), request.getLon());

        List<Double> pointCoordinates = List.of(point.getX(), point.getY());


        Set<String> imagesUrl = cloudinaryService.uploadMultiImg(files, request.getName());


        GeoFeature geoFeature = GeoFeature.builder()
                .address(request.getAddress())
                .name(request.getName())
                .open(request.getOpen())
                .close(request.getClose())
                .phone(request.getPhone())
                .disadvantage(request.getDisadvantage())
                .advantage(request.getAdvantage())
                .point(point)
                .images(imagesUrl)
                .build();

        GeoResponse geoResponse = GeoResponse.builder()
                .id(geoFeature.getId())
                .address(geoFeature.getAddress())
                .name(geoFeature.getName())
                .open(geoFeature.getOpen())
                .close(geoFeature.getClose())
                .phone(geoFeature.getPhone())
                .rate(geoFeature.getRate())
                .disadvantage(geoFeature.getDisadvantage())
                .advantage(geoFeature.getAdvantage())
                .point(pointCoordinates)
                .images(imagesUrl)
                .build();
        geoFeatureRepository.save(geoFeature);
        return geoResponse;
    }
    private Point createPointFromCoordinates(double lat, double lon) {
        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createPoint(new Coordinate(lon, lat)); // Kinh độ, Vĩ độ
    }

    public GeoResponse getGeoFeatureById(Long id) {
        GeoFeature geoFeature = geoFeatureRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "GeoFeature not found"));

        List<Double> pointCoordinates = List.of(geoFeature.getPoint().getY(), geoFeature.getPoint().getX());

        return GeoResponse.builder()
                .id(geoFeature.getId())
                .address(geoFeature.getAddress())
                .name(geoFeature.getName())
                .open(geoFeature.getOpen())
                .close(geoFeature.getClose())
                .phone(geoFeature.getPhone())
                .rate(geoFeature.getRate())
                .disadvantage(geoFeature.getDisadvantage())
                .advantage(geoFeature.getAdvantage())
                .point(pointCoordinates)
                .images(geoFeature.getImages())
                .build();
    }


    public List<GeoResponse> getAll() {
        List<GeoFeature> geoFeatures = geoFeatureRepository.findAll(); // Lấy tất cả GeoFeature từ repository

        return geoFeatures.stream()
                .map(geoFeature -> {
                    List<Double> pointCoordinates = List.of(geoFeature.getPoint().getY(), geoFeature.getPoint().getX());
                    return GeoResponse.builder()
                            .id(geoFeature.getId()) // Đảm bảo có id trong phản hồi
                            .address(geoFeature.getAddress())
                            .name(geoFeature.getName())
                            .open(geoFeature.getOpen())
                            .close(geoFeature.getClose())
                            .phone(geoFeature.getPhone())
                            .rate(geoFeature.getRate())
                            .disadvantage(geoFeature.getDisadvantage())
                            .advantage(geoFeature.getAdvantage())
                            .point(pointCoordinates)
                            .images(geoFeature.getImages())
                            .build();
                })
                .collect(Collectors.toList()); // Chuyển đổi danh sách GeoFeature thành danh sách GeoResponse
    }

    @PreAuthorize("hasRole('ADMIN')")
    public GeoResponse updateFeatures(Long id, GeoRequest request, List<MultipartFile> files) throws IOException {
        // Kiểm tra xem GeoFeature có tồn tại không
        GeoFeature geoFeature = geoFeatureRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "GeoFeature not found"));

        // Cập nhật thông tin từ request
        geoFeature.setName(request.getName());
        geoFeature.setAddress(request.getAddress());
        geoFeature.setPhone(request.getPhone());
        geoFeature.setOpen(request.getOpen());
        geoFeature.setClose(request.getClose());
        geoFeature.setAdvantage(request.getAdvantage());
        geoFeature.setDisadvantage(request.getDisadvantage());

        Point point = createPointFromCoordinates(request.getLat(), request.getLon());
        geoFeature.setPoint(point);

        // Nếu có hình ảnh mới, upload và cập nhật
        if (files != null && !files.isEmpty()) {
            Set<String> imagesUrl = cloudinaryService.uploadMultiImg(files, request.getName());
            geoFeature.setImages(imagesUrl);
        }

        // Lưu lại GeoFeature đã cập nhật
        GeoFeature updatedGeoFeature = geoFeatureRepository.save(geoFeature);

        // Tạo GeoResponse từ GeoFeature đã cập nhật
        return GeoResponse.builder()
                .id(updatedGeoFeature.getId())
                .name(updatedGeoFeature.getName())
                .open(updatedGeoFeature.getOpen())
                .close(updatedGeoFeature.getClose())
                .phone(updatedGeoFeature.getPhone())
                .rate(updatedGeoFeature.getRate())
                .disadvantage(updatedGeoFeature.getDisadvantage())
                .advantage(updatedGeoFeature.getAdvantage())
                .address(updatedGeoFeature.getAddress())
                .images(updatedGeoFeature.getImages())
                .reviews(updatedGeoFeature.getReviews())
                .point(List.of(updatedGeoFeature.getPoint().getY(), updatedGeoFeature.getPoint().getX()))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteFeatures(Long id) {
        GeoFeature geoFeature = geoFeatureRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "GeoFeature not found"));

        geoFeatureRepository.delete(geoFeature);
    }

}
