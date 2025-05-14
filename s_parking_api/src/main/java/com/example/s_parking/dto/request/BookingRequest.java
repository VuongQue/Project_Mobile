package com.example.s_parking.dto.request;

import javax.validation.constraints.NotNull;

public class BookingRequest{

    @NotNull(message = "ID bãi đậu xe không được để trống")
    private Long idParking;

    @NotNull(message = "Tên người dùng không được để trống")
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
