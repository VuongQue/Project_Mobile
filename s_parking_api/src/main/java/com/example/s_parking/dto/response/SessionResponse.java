package com.example.s_parking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionResponse {
    private Long id;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private String type;
    private String licensePlate;
    private float fee;
    private String username;
    private Long idParking;
    private Long idPayment;
}
