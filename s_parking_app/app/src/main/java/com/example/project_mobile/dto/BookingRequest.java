package com.example.project_mobile.dto;

public class BookingRequest {
    private Long idParking;
    private String username;

    public BookingRequest(long idParking, String username) {
        this.idParking = idParking;
        this.username = username;

    }
}
