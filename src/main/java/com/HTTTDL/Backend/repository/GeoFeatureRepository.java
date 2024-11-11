package com.HTTTDL.Backend.repository;


import com.HTTTDL.Backend.model.GeoFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface GeoFeatureRepository extends JpaRepository<GeoFeature, String> {
    boolean existsByName(String name);
    List<GeoFeature> findByRateGreaterThanEqual(float star);
    List<GeoFeature> findByRateBetween(float start, float end);
    @Query(value = "SELECT gf1_0.id, gf1_0.address, gf1_0.advantage, gf1_0.close, gf1_0.disadvantage, " +
            "gf1_0.name, gf1_0.open, gf1_0.phone, gf1_0.point, gf1_0.rate " +
            "FROM geo_features gf1_0 " +
            "WHERE ST_Distance_Sphere(gf1_0.point, Point(:lon, :lat)) <= :distance",
            nativeQuery = true)    List<GeoFeature> findWithinDistance(@Param("lat") double lat,
                                        @Param("lon") double lon,
                                        @Param("distance") double distance);
    @Query("SELECT g FROM GeoFeature g WHERE :time BETWEEN g.open AND g.close")
    List<GeoFeature> findOpenGeoFeatures(@Param("time") LocalTime time);
}
