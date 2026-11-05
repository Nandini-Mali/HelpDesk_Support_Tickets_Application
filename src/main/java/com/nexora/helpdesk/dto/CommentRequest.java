package com.nexora.helpdesk.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CommentRequest {
	@NotBlank(message="Comment Message is required")
	@Size(max=1000)
	private String message;
	 
	public CommentRequest() {
		
	}

	public CommentRequest(@NotBlank(message = "Comment Message is required") @Size(max = 1000) String message) {
		super();
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	

}
