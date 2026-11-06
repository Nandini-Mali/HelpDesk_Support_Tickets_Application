package com.nexora.helpdesk.dto;

import java.time.LocalDateTime;

public class TicketHistoryResponse {
    private Long id;
    private Long ticketId;
    private String changedByName;
    private String fieldChanged;
    private String oldValue;
    private String newValue;
    private LocalDateTime changedAt;

    public TicketHistoryResponse() {
    }

	public TicketHistoryResponse(Long id, Long ticketId, String changedByName, String fieldChanged, String oldValue,
			String newValue, LocalDateTime changedAt) {
		super();
		this.id = id;
		this.ticketId = ticketId;
		this.changedByName = changedByName;
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

	public Long getTicketId() {
		return ticketId;
	}

	public void setTicketId(Long ticketId) {
		this.ticketId = ticketId;
	}

	public String getChangedByName() {
		return changedByName;
	}

	public void setChangedByName(String changedByName) {
		this.changedByName = changedByName;
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
