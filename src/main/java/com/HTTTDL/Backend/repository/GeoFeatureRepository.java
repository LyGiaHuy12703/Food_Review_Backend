package com.HTTTDL.Backend.repository;


import com.HTTTDL.Backend.model.GeoFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeoFeatureRepository extends JpaRepository<GeoFeature, String> {
    boolean existsByName(String name);
    List<GeoFeature> findByRateGreaterThanEqual(float star);
    @Query("SELECT g FROM GeoFeature g WHERE ST_DistanceSphere(g.point, ST_SetSRID(ST_Point(:lon, :lat), 4326)) <= :distance")
    List<GeoFeature> findWithinDistance(@Param("lat") double lat,
                                        @Param("lon") double lon,
                                        @Param("distance") double distance);
}
