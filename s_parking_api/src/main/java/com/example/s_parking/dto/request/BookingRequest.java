package com.example.s_parking.dto.request;



public class BookingRequest{


    private Long idParking;

    private String username;

    public BookingRequest() {
    }

    public BookingRequest(Long idParking, String username) {
        this.idParking = idParking;
        this.username = username;
    }

    public Long getIdParking() {
        return idParking;
    }

    public void setIdParking(Long idParking) {
        this.idParking = idParking;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
