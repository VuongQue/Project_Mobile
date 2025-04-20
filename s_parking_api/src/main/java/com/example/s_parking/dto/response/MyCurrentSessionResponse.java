package com.example.s_parking.dto.response;

import com.example.s_parking.entity.ParkingLot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyCurrentSessionResponse {
    private Long id;

    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private String licensePlate;
    private String type;
    private float fee;

    private String location;

}
