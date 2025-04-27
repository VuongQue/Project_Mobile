package com.example.project_mobile.dto;

public class ParkingLotResponse {
    private Long id;
    private String area;
    private String row;
    private String pos;
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public ParkingLotResponse(Long id, String area, String row, String pos, String status) {
        this.id = id;
        this.area = area;
        this.row = row;
        this.pos = pos;
        this.status = status;
    }

    public String getLocation() {
        return area + " - " + row + pos;
    }
}
