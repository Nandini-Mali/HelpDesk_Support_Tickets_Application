
package com.nexora.helpdesk.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.helpdesk.dto.CommentRequest;
import com.nexora.helpdesk.dto.CommentResponse;
import com.nexora.helpdesk.service.CommentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // Add comment to ticket
    @PostMapping("/ticket/{ticketId}")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long ticketId,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication) {

        CommentResponse response = commentService.addComment(
                ticketId, authentication.getName(), request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Get all comments by ticket ID
    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByTicketId(
            @PathVariable Long ticketId,
            Authentication authentication) {

        List<CommentResponse> comments =
                commentService.getCommentsByTicketId(
                        ticketId, authentication.getName());

        return ResponseEntity.ok(comments);
    }

    // Get comment by ID
    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponse> getCommentById(
            @PathVariable Long commentId,
            Authentication authentication) {

        CommentResponse comment = commentService.getCommentById(
                commentId, authentication.getName());

        return ResponseEntity.ok(comment);
    }

    // Delete comment
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            Authentication authentication) {

        commentService.deleteComment(
                commentId, authentication.getName());

        return ResponseEntity.noContent().build();
    }
}
