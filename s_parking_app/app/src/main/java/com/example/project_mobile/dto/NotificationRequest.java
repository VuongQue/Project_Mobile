package com.example.project_mobile.dto;

public class NotificationRequest {
    private String username;
    private Long id;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotificationRequest(String username, Long id) {
        this.username = username;
        this.id = id;
    }
}
