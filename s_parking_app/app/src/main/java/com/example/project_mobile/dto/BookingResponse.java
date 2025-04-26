package com.example.project_mobile.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookingResponse {
    private Long id;
    private LocalDate date;
    private LocalDateTime createdAt;
    private float fee;
    private String location;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public float getFee() {
        return fee;
    }

    public void setFee(float fee) {
        this.fee = fee;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public BookingResponse(Long id, LocalDate date, LocalDateTime createdAt, float fee, String location) {
        this.id = id;
        this.date = date;
        this.createdAt = createdAt;
        this.fee = fee;
        this.location = location;
    }
}