package com.example.s_parking.service;

import com.example.s_parking.dto.response.ParkingAreaResponse;
import com.example.s_parking.entity.ParkingArea;

import java.util.List;

public interface ParkingAreaService {
    List<ParkingArea> getParkingAreas();

    ParkingAreaResponse convertToDto(ParkingArea entity);

    List<ParkingAreaResponse> convertAllToDto(List<ParkingArea> list);

    void updateSlots();
}
