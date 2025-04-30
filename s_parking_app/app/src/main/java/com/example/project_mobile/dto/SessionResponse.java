package com.example.project_mobile.dto;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

public class SessionResponse {
    private Long id;
    private String username;
    private Long idParking;
    private String licensePlate;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Long idPayment;
    private float fee;
    private String type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getIdParking() {
        return idParking;
    }

    public void setIdParking(Long idParking) {
        this.idParking = idParking;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
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

    public Long getIdPayment() {
        return idPayment != null ? idPayment : -1;
    }

    public void setIdPayment(Long idPayment) {
        this.idPayment = idPayment;
    }

    public float getFee() {
        return fee;
    }

    public void setFee(float fee) {
        this.fee = fee;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public SessionResponse(Long id, String username, Long idParking, String licensePlate, LocalDateTime checkIn, LocalDateTime checkOut, Long idPayment, float fee, String type) {
        this.id = id;
        this.username = username;
        this.idParking = idParking;
        this.licensePlate = licensePlate;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.idPayment = idPayment;
        this.fee = fee;
        this.type = type;
    }
}