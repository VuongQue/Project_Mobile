package com.example.s_parking.dto.request;


import lombok.Getter;

@Getter
public class BookingRequest{


    private Long idParking;

    private String username;

    public BookingRequest() {
    }

    public BookingRequest(Long idParking, String username) {
        this.idParking = idParking;
        this.username = username;
    }

    public void setIdParking(Long idParking) {
        this.idParking = idParking;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
