package com.example.project_mobile.dto;

public class ParkingAreaResponse {
    private String idArea;
    private int maxCapacity;
    private int availableSlots;
    private String status;

    public String getIdArea() {
        return idArea;
    }

    public void setIdArea(String idArea) {
        this.idArea = idArea;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public int getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(int availableSlots) {
        this.availableSlots = availableSlots;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ParkingAreaResponse(String idArea, int maxCapacity, int availableSlots, String status) {
        this.idArea = idArea;
        this.maxCapacity = maxCapacity;
        this.availableSlots = availableSlots;
        this.status = status;
    }
    @Override
    public String toString() {
        return "ParkingAreaResponse{" +
                "idArea='" + idArea + '\'' +
                ", maxCapacity=" + maxCapacity +
                ", availableSlots=" + availableSlots +
                ", status='" + status + '\'' +
                '}';
    }

}
