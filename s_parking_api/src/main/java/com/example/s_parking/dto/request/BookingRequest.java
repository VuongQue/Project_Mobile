package com.example.s_parking.dto.request;


import lombok.Getter;
import lombok.Setter;

@Getter
public class BookingRequest{

    private Long id;
    @Setter
    private Long idParking;

    @Setter
    private String username;

    public BookingRequest() {
    }

    public BookingRequest(Long idParking, String username) {
        this.idParking = idParking;
        this.username = username;
    }

    public void getId(long id) {
        this.id = id;
    }
}
