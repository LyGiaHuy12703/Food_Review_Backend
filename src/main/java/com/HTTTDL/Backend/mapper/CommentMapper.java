package com.HTTTDL.Backend.mapper;

import com.HTTTDL.Backend.dto.comment.CommentResponse;
import com.HTTTDL.Backend.model.Review;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    List<CommentResponse> toCommentResponse(List<Review> reviews);
}
