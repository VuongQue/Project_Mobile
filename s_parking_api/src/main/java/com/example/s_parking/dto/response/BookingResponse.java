package com.example.s_parking.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingResponse {

    private Long id;
    private LocalDateTime createdAt;
    private LocalDate date;
    private float fee;
    private Long idParking;
    private String username;
    private Long idPayment;
}
