package com.example.project_mobile.model;

import java.util.Date;

public class Session {
    private long id;
    private String username;
    private String position;
    private String licensePlate;
    private Date checkIn;
    private Date checkOut;
    private long idPayment;
    private float fee;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
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

    public long getIdPayment() {
        return idPayment;
    }

    public void setIdPayment(long idPayment) {
        idPayment = idPayment;
    }

    public float getFee() {
        return fee;
    }

    public void setFee(float fee) {
        this.fee = fee;
    }

    public Session(long id, String username, String position, String licensePlate, Date checkIn, Date checkOut, long idPayment, float fee) {
        this.id = id;
        this.username = username;
        this.position = position;
        this.licensePlate = licensePlate;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.idPayment = idPayment;
        this.fee = fee;
    }
}
