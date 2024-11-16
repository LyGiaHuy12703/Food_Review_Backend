package com.HTTTDL.Backend.controller;

import com.HTTTDL.Backend.dto.Api.ApiResponse;
import com.HTTTDL.Backend.dto.comment.ReviewResponse;
import com.HTTTDL.Backend.dto.comment.CreateReviewRequest;
import com.HTTTDL.Backend.dto.comment.UpdateReviewRequest;
import com.HTTTDL.Backend.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @PostMapping("/create")
    ResponseEntity<ApiResponse<ReviewResponse>> createComment(@RequestBody CreateReviewRequest request) {
        ApiResponse<ReviewResponse> comment = ApiResponse.<ReviewResponse>builder()
                .code("comment-s-01")
                .data(reviewService.createComment(request))
                .message("Create comment success")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<List<ReviewResponse>>> getCommentById(@PathVariable String id) {
        ApiResponse<List<ReviewResponse>> comment = ApiResponse.<List<ReviewResponse>>builder()
                .code("comment-s-02")
                .data(reviewService.getPositionComment(id))
                .message("get comment success")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(comment);
    }
    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<ReviewResponse>> putCommentById(@PathVariable String id, @RequestBody UpdateReviewRequest request) {
        ApiResponse<ReviewResponse> comment = ApiResponse.<ReviewResponse>builder()
                .code("comment-s-03")
                .data(reviewService.updateComment(id, request))
                .message("update comment success")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }
    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<ReviewResponse>> deleteCommentById(@PathVariable String id) {
        reviewService.deleteComment(id);
        ApiResponse<ReviewResponse> comment = ApiResponse.<ReviewResponse>builder()
                .code("comment-s-04")
                .message("Delete comment success")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(comment);
    }
    //method for admin
    @DeleteMapping("/admin/{id}")
    ResponseEntity<ApiResponse<ReviewResponse>> deleteCommentByIdForAdmin(@PathVariable String id) {
        reviewService.deleteCommentForAdmin(id);
        ApiResponse<ReviewResponse> comment = ApiResponse.<ReviewResponse>builder()
                .code("comment-s-05")
                .message("Delete comment success")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(comment);
    }
}
