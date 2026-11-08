package com.nexora.helpdesk.entity;

import java.time.LocalDateTime;

import com.nexora.helpdesk.enums.Priority;
import com.nexora.helpdesk.enums.TicketStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "tickets")
public class Ticket {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable=false,length=150)
	private String title;
	
	@Column(length=2000,nullable=false)
    private String description;
	
	@Column(nullable=false,length=100)
	private String category;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	private Priority priority;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	private TicketStatus status;
	
	@ManyToOne
    @JoinColumn(name = "created_by",nullable=false)
	private User createdBy;
	
	private LocalDateTime createdAt;

    private LocalDateTime closedAt;

    public Ticket() {
    	
    }
    
	public Ticket(Long id, String title, String description, String category, Priority priority, TicketStatus status,
			User createdBy, LocalDateTime createdAt, LocalDateTime closedAt) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.category = category;
		this.priority = priority;
		this.status = status;
		this.createdBy = createdBy;
		this.createdAt = createdAt;
		this.closedAt = closedAt;
	}
	
	@PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();

        if (status == null) {
            status = TicketStatus.OPEN;
        }
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

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
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
