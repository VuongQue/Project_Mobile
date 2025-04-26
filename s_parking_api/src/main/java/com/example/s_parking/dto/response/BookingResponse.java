package com.example.s_parking.dto.response;

import com.example.s_parking.entity.ParkingLot;
import com.example.s_parking.entity.Payment;
import com.example.s_parking.entity.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
    private Long id;
    private LocalDate date;
    private LocalDateTime createdAt;
    private float fee;
    private String location;
}
