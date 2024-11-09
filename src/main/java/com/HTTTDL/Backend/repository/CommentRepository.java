package com.HTTTDL.Backend.repository;

import com.HTTTDL.Backend.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Review, Long> {
    List<Review> findByGeoFeaturesId(Long id);
}
