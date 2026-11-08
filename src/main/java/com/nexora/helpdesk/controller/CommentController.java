package com.nexora.helpdesk.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
		this.commentService=commentService;
	}

//Add comment to ticket
@PostMapping("/ticket/{ticketId}")
  public ResponseEntity<CommentResponse> addComment(@PathVariable Long ticketId,@RequestParam Long userId,@Valid @RequestBody CommentRequest request) {
  CommentResponse response=commentService.addComment(ticketId, userId, request);
  return new ResponseEntity<>(response, HttpStatus.CREATED);
}

//Get All Comments By Ticket ID
@GetMapping("/ticket/{ticketId}")
  public ResponseEntity<List<CommentResponse>> getCommentsByTicketId(@PathVariable Long ticketId,@RequestParam Long userId) {
  List<CommentResponse> comments =commentService.getCommentsByTicketId(ticketId,userId);
  return ResponseEntity.ok(comments);
}

// Get Comment By ID
  @GetMapping("/{commentId}")
   public ResponseEntity<CommentResponse> getCommentById(@PathVariable Long commentId,@RequestParam Long userId) {
   CommentResponse comment =commentService.getCommentById(commentId,userId);
    return ResponseEntity.ok(comment);
}

// Delete Comment
@DeleteMapping("/{commentId}")
   public ResponseEntity<Void> deleteComment(@PathVariable Long commentId,@RequestParam Long userId) {
    commentService.deleteComment(commentId,userId);
    return ResponseEntity.noContent().build();
    }
}
 

