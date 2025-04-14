package com.example.s_parking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingLotResponse {
    private long id;
    private String area;
    private String row;
    private String pos;
    private String status;
}
