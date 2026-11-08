package com.nexora.helpdesk.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name="comments")
public class Comment {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="ticket_id",nullable=false)
	private Ticket ticket;
	
	@ManyToOne
	@JoinColumn(name="author_id",nullable=false)
	private User author;
	
	@Column(nullable=false,length=1000)
	private String message;
	private LocalDateTime createdAt;
	
	public Comment() {
		
	}
	@PrePersist
	protected void onCreate() {
	    createdAt = LocalDateTime.now();
	}
	

	public Comment(Long id, Ticket ticket, User author, String message, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.ticket = ticket;
		this.author = author;
		this.message = message;
		this.createdAt = createdAt;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Ticket getTicket() {
		return ticket;
	}

	public void setTicket(Ticket ticket) {
		this.ticket = ticket;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt =createdAt;
	}

}
