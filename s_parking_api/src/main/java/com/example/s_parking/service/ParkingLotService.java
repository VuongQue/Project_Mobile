package com.example.s_parking.service;

import com.example.s_parking.dto.response.ParkingLotResponse;
import com.example.s_parking.entity.ParkingLot;

import java.util.List;
import java.util.Optional;

public interface ParkingLotService {
    List<ParkingLot> getAllParkingLots();
    ParkingLotResponse convertToDto(ParkingLot entity);
    List<ParkingLotResponse> convertAllToDto(List<ParkingLot> list);
    Optional<ParkingLot> getParkingLotById(Long id);
    ParkingLot createParkingLot(ParkingLot booking);
    ParkingLot updateParkingLot(ParkingLot parkingLot);
    void deleteParkingLot(Long id);

    Optional<ParkingLot> getSlot();

    List<ParkingLot> getAvailableParkingLots();
}
