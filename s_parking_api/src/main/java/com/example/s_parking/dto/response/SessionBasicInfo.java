package com.example.s_parking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionBasicInfo {
    private Long id;
    private String username;
    private String location;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private String licensePlate;
    private float fee;
    private Long idPayment;
}
