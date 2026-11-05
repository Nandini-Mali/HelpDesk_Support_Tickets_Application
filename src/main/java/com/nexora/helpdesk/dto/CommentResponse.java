package com.nexora.helpdesk.dto;

import java.time.LocalDateTime;

public class CommentResponse {

	private Long id;
	private String message;
	private String authorName;
	private LocalDateTime createdAt;
	
	public CommentResponse() {
		
	}

	public CommentResponse(Long id, String message, String authorName, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.message = message;
		this.authorName = authorName;
		this.createdAt = createdAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
}
