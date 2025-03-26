package com.example.project_mobile.model;

public class ParkingLot {
    private String area;
    private String row;
    private String pos;

    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public ParkingLot(String area, String row, String pos, String state) {
        this.area = area;
        this.row = row;
        this.pos = pos;
        this.status = state;
    }

    public String getName() {
        return area + " - " + row + pos;
    }
}
