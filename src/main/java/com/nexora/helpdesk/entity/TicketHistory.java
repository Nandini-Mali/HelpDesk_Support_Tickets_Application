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
@Table(name="ticket_history")
public class TicketHistory {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="ticket_id",nullable=false)
	private Ticket ticket;

	@ManyToOne
	@JoinColumn(name="changed_by",nullable=false)
	private User changedBy;
	
	private String fieldChanged;
	
	@Column(length=2000)
	private String oldValue;
	
	@Column(length=2000)
	private String newValue;

	private LocalDateTime changedAt;
	
	@PrePersist
	protected void onCreate() {
	    changedAt = LocalDateTime.now();
	}
	public TicketHistory() {
		
	}
	public TicketHistory(Long id, Ticket ticket, User changedBy, String fieldChanged, String oldValue, String newValue,
			LocalDateTime changedAt) {
		super();
		this.id = id;
		this.ticket = ticket;
		this.changedBy = changedBy;
		this.fieldChanged = fieldChanged;
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.changedAt = changedAt;
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
	public User getChangedBy() {
		return changedBy;
	}
	public void setChangedBy(User changedBy) {
		this.changedBy = changedBy;
	}
	public String getFieldChanged() {
		return fieldChanged;
	}
	public void setFieldChanged(String fieldChanged) {
		this.fieldChanged = fieldChanged;
	}
	public String getOldValue() {
		return oldValue;
	}
	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}
	public String getNewValue() {
		return newValue;
	}
	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
	public LocalDateTime getChangedAt() {
		return changedAt;
	}
	public void setChangedAt(LocalDateTime changedAt) {
		this.changedAt = changedAt;
	}
	

}
