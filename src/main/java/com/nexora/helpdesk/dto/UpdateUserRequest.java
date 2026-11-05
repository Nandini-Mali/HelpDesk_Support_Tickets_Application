package com.nexora.helpdesk.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateUserRequest {

	@NotBlank(message = "Name is required")
    @Size(min = 2, max = 100)
	private String name;
	
	@NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email")
	private String email;
	
	public UpdateUserRequest() {
		
	}

	public UpdateUserRequest(@NotBlank(message = "Name is required") @Size(min = 2, max = 100) String name,
			@NotBlank(message = "Email is required") @Email(message = "Enter a valid email") String email) {
		super();
		this.name = name;
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
}
