package com.nexora.helpdesk.desktop.model;

public class UserSession {

    private Long id;
    private String name;
    private String email;
    private String role;

    public UserSession(Long id, String name, String email, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public boolean isAgent() {
        return "AGENT".equalsIgnoreCase(role);
    }
}