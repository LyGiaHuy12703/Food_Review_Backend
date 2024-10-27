package com.HTTTDL.Backend.repository;


import com.HTTTDL.Backend.model.GeoFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GeoFeatureRepository extends JpaRepository<GeoFeature, Long> {
    boolean existsByName(String name);
}
