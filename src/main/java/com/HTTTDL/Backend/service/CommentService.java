package com.HTTTDL.Backend.service;

import com.HTTTDL.Backend.dto.Geo.GeoResponse;
import com.HTTTDL.Backend.dto.comment.CommentResponse;
import com.HTTTDL.Backend.dto.comment.CreateCommentRequest;
import com.HTTTDL.Backend.dto.comment.UpdateCommentRequest;
import com.HTTTDL.Backend.exception.AppException;
import com.HTTTDL.Backend.mapper.CommentMapper;
import com.HTTTDL.Backend.mapper.GeoMapper;
import com.HTTTDL.Backend.model.GeoFeature;
import com.HTTTDL.Backend.model.Review;
import com.HTTTDL.Backend.model.User;
import com.HTTTDL.Backend.repository.CommentRepository;
import com.HTTTDL.Backend.repository.GeoFeatureRepository;
import com.HTTTDL.Backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GeoFeatureRepository geoFeatureRepository;
    @Autowired
    private GeoMapper geoMapper;
    @Autowired
    private CommentMapper commentMapper;


    @PreAuthorize("hasRole('USER')")
    public CommentResponse createComment(CreateCommentRequest request) {
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
                .build();

        commentRepository.save(review);

        return CommentResponse.builder()
                .id(review.getId())
                .author(author)
                .feature(geoFeature)
                .comment(review.getComment())
                .rating(review.getRating())
                .build();
    }
    private void updateRate(Long geoFeatureId, int rating) {
        GeoFeature geoFeature = geoFeatureRepository.findById(geoFeatureId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Feature not found"));
        List<Review> reviews = commentRepository.findByGeoFeaturesId(geoFeatureId);

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

    public List<CommentResponse> getPositionComment(Long id) {
        List<Review> reviews = commentRepository.findByGeoFeaturesId(id);
        return commentMapper.toCommentResponse(reviews);
    }

    @PreAuthorize("hasRole('USER')")
    public CommentResponse updateComment(Long id, UpdateCommentRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User author = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User Not Found"));

        Review review = commentRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Review not found"));

        if(!review.getUser().equals(author)) {
            throw new AppException(HttpStatus.FORBIDDEN, "You are not authorized to update this comment");
        }
        updateRate(review.getGeoFeatures().getId(), request.getRating());
        review.setComment(request.getComment());
        review.setRating(request.getRating());
        commentRepository.save(review);
        return CommentResponse.builder()
                .id(review.getId())
                .author(author)
                .feature(review.getGeoFeatures())
                .comment(review.getComment())
                .rating(review.getRating())
                .build();
    }

    public void deleteComment(Long id) {
        Review review = commentRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Review not found"));
        commentRepository.delete(review);
    }
}
