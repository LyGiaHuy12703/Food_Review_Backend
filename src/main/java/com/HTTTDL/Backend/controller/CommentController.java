package com.HTTTDL.Backend.controller;

import com.HTTTDL.Backend.dto.Api.ApiResponse;
import com.HTTTDL.Backend.dto.comment.CommentResponse;
import com.HTTTDL.Backend.dto.comment.CreateCommentRequest;
import com.HTTTDL.Backend.dto.comment.UpdateCommentRequest;
import com.HTTTDL.Backend.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping("/create")
    ResponseEntity<ApiResponse<CommentResponse>> createComment(@RequestBody CreateCommentRequest request) {
        ApiResponse<CommentResponse> comment = ApiResponse.<CommentResponse>builder()
                .code("comment-s-01")
                .data(commentService.createComment(request))
                .message("Create comment success")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<List<CommentResponse>>> getCommentById(@PathVariable String id) {
        ApiResponse<List<CommentResponse>> comment = ApiResponse.<List<CommentResponse>>builder()
                .code("comment-s-02")
                .data(commentService.getPositionComment(id))
                .message("get comment success")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(comment);
    }
    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<CommentResponse>> putCommentById(@PathVariable String id, @RequestBody UpdateCommentRequest request) {
        ApiResponse<CommentResponse> comment = ApiResponse.<CommentResponse>builder()
                .code("comment-s-03")
                .data(commentService.updateComment(id, request))
                .message("update comment success")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }
    @DeleteMapping("{id}")
    ResponseEntity<ApiResponse<CommentResponse>> deleteCommentById(@PathVariable String id) {
        commentService.deleteComment(id);
        ApiResponse<CommentResponse> comment = ApiResponse.<CommentResponse>builder()
                .code("comment-s-04")
                .message("Delete comment success")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(comment);
    }
}
