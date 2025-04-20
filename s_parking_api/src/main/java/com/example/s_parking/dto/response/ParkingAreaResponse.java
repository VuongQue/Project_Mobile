package com.example.s_parking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingAreaResponse {
    private String idArea;
    private int maxCapacity;
    private int availableSlots;
    private String status;
}
