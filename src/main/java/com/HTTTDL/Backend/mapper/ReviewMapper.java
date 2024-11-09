package com.HTTTDL.Backend.mapper;

import com.HTTTDL.Backend.dto.comment.ReviewResponse;
import com.HTTTDL.Backend.model.Review;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    List<ReviewResponse> toReviewResponses(List<Review> reviews);
}
