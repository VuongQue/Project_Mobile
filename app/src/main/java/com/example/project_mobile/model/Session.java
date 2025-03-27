package com.example.project_mobile.model;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Date;

public class Session {
    private long id;
    private String mssv;
    private String position;
    private Date timeIn;
    private Date timeOut;
    private boolean isPaid;
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

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public float getFee() {
        return fee;
    }

    public void setFee(float fee) {
        this.fee = fee;
    }

    public Session(long id, String mssv, String position, Date timeIn, Date timeOut, boolean isPaid, float fee) {
        this.id = id;
        this.mssv = mssv;
        this.position = position;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.isPaid = isPaid;
        this.fee = fee;
    }
}
