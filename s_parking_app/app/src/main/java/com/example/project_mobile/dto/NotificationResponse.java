package com.example.project_mobile.dto;

import java.time.LocalDateTime;

public class NotificationResponse {
    private Long id;

    private String title;
    private String message;
    private LocalDateTime createdAt;

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
        this.createdAt = createdAt;
    }

    public NotificationResponse(Long id, String title, String message, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.createdAt = createdAt;
    }
}
