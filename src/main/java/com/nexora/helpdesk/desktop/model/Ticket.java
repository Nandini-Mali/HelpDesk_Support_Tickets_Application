package com.nexora.helpdesk.desktop.model;

public class Ticket {
	private Long id;
    private String title;
    private String description;
    private String category;
    private String priority;
    private String status;
    private String createdAt;
    private String closedAt;
    private String createdByName;

   public Ticket(){
	   
   }

public Ticket(Long id, String title, String description, String category, String priority, String status,
		String createdAt, String closedAt, String createdByName) {
	super();
	this.id = id;
	this.title = title;
	this.description = description;
	this.category = category;
	this.priority = priority;
	this.status = status;
	this.createdAt = createdAt;
	this.closedAt = closedAt;
	this.createdByName = createdByName;
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

public String getPriority() {
	return priority;
}

public void setPriority(String priority) {
	this.priority = priority;
}

public String getStatus() {
	return status;
}

public void setStatus(String status) {
	this.status = status;
}

public String getCreatedAt() {
	return createdAt;
}

public void setCreatedAt(String createdAt) {
	this.createdAt = createdAt;
}

public String getClosedAt() {
	return closedAt;
}

public void setClosedAt(String closedAt) {
	this.closedAt = closedAt;
}

public String getCreatedByName() {
	return createdByName;
}

public void setCreatedByName(String createdByName) {
	this.createdByName = createdByName;
}
   
   
}
 