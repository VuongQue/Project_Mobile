package com.example.project_mobile.dto;

import java.time.LocalDateTime;
import java.util.Optional;

public class MyCurrentSessionResponse {
    private Long id;
    private LocalDateTime checkIn;  // Dùng Optional cho checkIn
    private LocalDateTime checkOut;
    private String licensePlate;
    private String type;
    private float fee;
    private String location;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDateTime checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDateTime getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDateTime checkOut) {
        this.checkOut = checkOut;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public MyCurrentSessionResponse(Long id, LocalDateTime checkIn, LocalDateTime checkOut, String licensePlate, String type, float fee, String location) {
        this.id = id;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.licensePlate = licensePlate;
        this.type = type;
        this.fee = fee;
        this.location = location;
    }
}
