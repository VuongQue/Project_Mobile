package com.example.project_mobile.model;

import java.util.Date;

public class Session {
    private long id;
    private String username;
    private long idParking;
    private String licensePlate;
    private Date checkIn;
    private Date checkOut;
    private long idPayment;
    private float fee;
    private String type;

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

    public long getIdParking() {
        return idParking;
    }

    public void setIdParking(long idParking) {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Session(long id, String username, long idParking, String licensePlate, Date checkIn, Date checkOut, long idPayment, float fee, String type) {
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
