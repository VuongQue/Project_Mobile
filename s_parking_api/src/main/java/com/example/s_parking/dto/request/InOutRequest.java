package com.example.s_parking.dto.request;

import lombok.Data;

@Data
public class InOutRequest {
    String username;
    String licensePlate;
    String areaId;
}
