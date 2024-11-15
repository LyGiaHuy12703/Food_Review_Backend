package com.HTTTDL.Backend.service;

import com.HTTTDL.Backend.dto.comment.ReviewResponse;
import com.HTTTDL.Backend.dto.comment.CreateReviewRequest;
import com.HTTTDL.Backend.dto.comment.UpdateReviewRequest;
import com.HTTTDL.Backend.exception.AppException;
import com.HTTTDL.Backend.mapper.ReviewMapper;
import com.HTTTDL.Backend.mapper.GeoMapper;
import com.HTTTDL.Backend.model.GeoFeature;
import com.HTTTDL.Backend.model.Review;
import com.HTTTDL.Backend.model.User;
import com.HTTTDL.Backend.repository.ReviewRepository;
import com.HTTTDL.Backend.repository.GeoFeatureRepository;
import com.HTTTDL.Backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GeoFeatureRepository geoFeatureRepository;
    @Autowired
    private GeoMapper geoMapper;
    @Autowired
    private ReviewMapper reviewMapper;


    @PreAuthorize("hasRole('USER')")
    public ReviewResponse createComment(CreateReviewRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User author = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User Not Found"));
        GeoFeature geoFeature = geoFeatureRepository.findById(request.getPositionId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "GeoFeature Not Found"));

        updateRate(request.getPositionId(), request.getRating());

        Review review = Review.builder()
                .comment(request.getComment())
                .geoFeatures(geoFeature)
                .user(author)
                .rating(request.getRating())
                .createdAt(LocalDateTime.now())
                .build();

        reviewRepository.save(review);

        return ReviewResponse.builder()
                .id(review.getId())
                .user(author)
                .geoFeatures(geoFeature)
                .comment(review.getComment())
                .rating(review.getRating())
                .createdAt(LocalDateTime.now())
                .build();
    }
    private void updateRate(String geoFeatureId, int rating) {
        GeoFeature geoFeature = geoFeatureRepository.findById(geoFeatureId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Feature not found"));
        List<Review> reviews = reviewRepository.findByGeoFeaturesId(geoFeatureId);

        // Tính tổng số điểm từ tất cả các đánh giá
        int totalRating = reviews.stream().mapToInt(Review::getRating).sum();

        // Thêm điểm đánh giá mới vào tổng điểm
        totalRating += rating;

        // Tính tổng số lần đánh giá (bao gồm cả đánh giá mới)
        int numberOfRatings = reviews.size() + 1;
        float averageRate = (float) totalRating / numberOfRatings;

        geoFeature.setRate(averageRate);
        geoFeatureRepository.save(geoFeature);
    }

    public List<ReviewResponse> getPositionComment(String id) {
        List<Review> reviews = reviewRepository.findByGeoFeaturesId(id);
        return reviewMapper.toReviewResponses(reviews);
    }

    @PreAuthorize("hasRole('USER')")
    public ReviewResponse updateComment(String id, UpdateReviewRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User author = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User Not Found"));

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Review not found"));

        if(!review.getUser().equals(author)) {
            throw new AppException(HttpStatus.FORBIDDEN, "You are not authorized to update this comment");
        }
        updateRate(review.getGeoFeatures().getId(), request.getRating());
        review.setComment(request.getComment());
        review.setRating(request.getRating());
        reviewRepository.save(review);
        return ReviewResponse.builder()
                .id(review.getId())
                .user(author)
                .geoFeatures(review.getGeoFeatures())
                .comment(review.getComment())
                .rating(review.getRating())
                .build();
    }

    @PreAuthorize("hasRole('USER')")
    public void deleteComment(String id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User Not Found"));
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Review not found"));
        if(!review.getUser().equals(user)) {
            throw new AppException(HttpStatus.FORBIDDEN, "You are not authorized to delete this comment");
        }
        reviewRepository.delete(review);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCommentForAdmin(String id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Review not found"));
        reviewRepository.delete(review);
    }
}
