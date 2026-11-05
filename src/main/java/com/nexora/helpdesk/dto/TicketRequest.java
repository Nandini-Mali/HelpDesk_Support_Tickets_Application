package com.nexora.helpdesk.dto;

import com.nexora.helpdesk.enums.Priority;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TicketRequest {
    @NotBlank(message="Title is required")
    @Size(max=150)
	private String title;
	
    @NotBlank(message="Description is required")
    private String description;
	
    @NotBlank(message="Category is required")
	private String category;
	
    @NotNull(message="Priority is requred")
	private Priority priority;
    
    public TicketRequest() {
    	
    }

	public TicketRequest(@NotBlank(message = "Title is required") @Size(max = 150) String title,
			@NotBlank(message = "Description is required") String description,
			@NotBlank(message = "Category is required") String category,
			@NotNull(message = "Priority is requred") Priority priority) {
		super();
		this.title = title;
		this.description = description;
		this.category = category;
		this.priority = priority;
	}

    public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}
	
    
}
