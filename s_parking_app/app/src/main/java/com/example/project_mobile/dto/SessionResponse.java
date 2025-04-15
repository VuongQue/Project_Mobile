package com.example.project_mobile.dto;

import java.util.Date;

public class SessionResponse {
    private Long id;
    private String username;
    private Long idParking;
    private String licensePlate;
    private Date checkIn;
    private Date checkOut;
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

    public Date getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(Date checkIn) {
        this.checkIn = checkIn;
    }

    public Date getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(Date checkOut) {
        this.checkOut = checkOut;
    }

    public Long getIdPayment() {
        return idPayment;
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

    public SessionResponse(Long id, String username, Long idParking, String licensePlate, Date checkIn, Date checkOut, Long idPayment, float fee, String type) {
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
