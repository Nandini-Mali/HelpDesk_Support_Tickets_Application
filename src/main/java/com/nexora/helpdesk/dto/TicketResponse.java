package com.nexora.helpdesk.dto;

import java.time.LocalDateTime;

import com.nexora.helpdesk.enums.Priority;
import com.nexora.helpdesk.enums.TicketStatus;

public class TicketResponse {

	private Long id;
	private String title;
	private String description;
	private String category;
	private Priority priority;
	private TicketStatus status;
	private LocalDateTime createdAt;
	private LocalDateTime closedAt;
	
	public TicketResponse() {
		
	}

	public TicketResponse(Long id, String title, String description, String category, Priority priority,
			TicketStatus status, LocalDateTime createdAt, LocalDateTime closedAt) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.category = category;
		this.priority = priority;
		this.status = status;
		this.createdAt = createdAt;
		this.closedAt = closedAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public TicketStatus getStatus() {
		return status;
	}

	public void setStatus(TicketStatus status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getClosedAt() {
		return closedAt;
	}

	public void setClosedAt(LocalDateTime closedAt) {
		this.closedAt = closedAt;
	}
	
}
