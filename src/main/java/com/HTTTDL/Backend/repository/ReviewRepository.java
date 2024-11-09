package com.HTTTDL.Backend.repository;

import com.HTTTDL.Backend.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, String> {
    List<Review> findByGeoFeaturesId(String id);
}
