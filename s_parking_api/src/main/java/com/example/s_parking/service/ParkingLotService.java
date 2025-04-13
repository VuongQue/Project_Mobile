package com.example.s_parking.service;

import com.example.s_parking.dto.request.ParkingLotRequest;
import com.example.s_parking.entity.Booking;
import com.example.s_parking.entity.ParkingLot;

import java.util.List;
import java.util.Optional;

public interface ParkingLotService {
    List<ParkingLot> getAllParkingLots();
    ParkingLotRequest convertToDto(ParkingLot entity);
    List<ParkingLotRequest> convertAllToDto(List<ParkingLot> list);
    Optional<ParkingLot> getParkingLotById(Long id);
    ParkingLot createParkingLot(ParkingLot booking);
    ParkingLot updateParkingLot(Long id, ParkingLot parkingLot);
    void deleteParkingLot(Long id);
}
