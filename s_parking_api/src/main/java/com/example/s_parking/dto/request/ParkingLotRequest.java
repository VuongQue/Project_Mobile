package com.example.s_parking.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingLotRequest {
    public long id;
    public String area;
    public String row;
    public String pos;
    public String status;
}
