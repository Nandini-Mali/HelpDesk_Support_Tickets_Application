package com.nexora.helpdesk.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
	@NotBlank(message="Name is required")
	@Size(min=2,max=100)
	private String name;
	
	@NotBlank(message="Email is required")
	@Email(message="Enterr a valid email")
	private String email;
	
	@NotBlank(message="Password is required")
	@Size(min=8,max=100)
	private String password;
	
	public RegisterRequest() {
		
	}

	public RegisterRequest(@NotBlank(message = "Name is required") @Size(min = 2, max = 100) String name,
			@NotBlank(message = "Email is required") @Email(message = "Enterr a valid email") String email,
			@NotBlank(message = "Password is required") @Size(min = 8, max = 100) String password) {
		super();
		this.name = name;
		this.email = email;
		this.password = password;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	

}