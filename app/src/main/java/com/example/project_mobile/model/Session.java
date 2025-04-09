package com.example.project_mobile.model;

import java.util.Date;

public class Session {
    private long id;
    private String mssv;
    private String position;
    private Date timeIn;
    private Date timeOut;
    private long idPayment;
    private float fee;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMssv() {
        return mssv;
    }

    public void setMssv(String mssv) {
        this.mssv = mssv;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Date getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(Date timeIn) {
        this.timeIn = timeIn;
    }

    public Date getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Date timeOut) {
        this.timeOut = timeOut;
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

    public Session(long id, String mssv, String position, Date timeIn, Date timeOut, long idPayment, float fee) {
        this.id = id;
        this.mssv = mssv;
        this.position = position;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.idPayment = idPayment;
        this.fee = fee;
    }
}
