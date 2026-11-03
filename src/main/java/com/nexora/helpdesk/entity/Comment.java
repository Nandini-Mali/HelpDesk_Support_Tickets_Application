package com.nexora.helpdesk.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
	
	@Column(nullable=false,length=2000)
	private String message;
	private LocalDateTime creatdetAt;
	
	public Comment() {
		
	}
	

	public Comment(Long id, Ticket ticket, User author, String message, LocalDateTime creatdetAt) {
		super();
		this.id = id;
		this.ticket = ticket;
		this.author = author;
		this.message = message;
		this.creatdetAt = creatdetAt;
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

	public LocalDateTime getCreatdetAt() {
		return creatdetAt;
	}

	public void setCreatdetAt(LocalDateTime creatdetAt) {
		this.creatdetAt = creatdetAt;
	}

}
