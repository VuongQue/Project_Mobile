package com.example.s_parking.implement;

import com.example.s_parking.dto.response.ParkingAreaResponse;
import com.example.s_parking.entity.ParkingArea;
import com.example.s_parking.repository.ParkingAreaRepository;
import com.example.s_parking.service.ParkingAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ParkingAreaImp implements ParkingAreaService {

    @Autowired
    private ParkingAreaRepository parkingAreaRepository;

    @Override
    public List<ParkingArea> getParkingAreas() {
        return parkingAreaRepository.findAll();
    }

    @Override
    public ParkingAreaResponse convertToDto(ParkingArea entity) {
        return ParkingAreaResponse.builder()
                .idArea(entity.getIdArea())
                .availableSlots(entity.getAvailableSlots())
                .maxCapacity(entity.getMaxCapacity())
                .status(Objects.equals(entity.getAvailableSlots(), entity.getMaxCapacity()) ? "Unavailable" : "Available")
                .build();
    }

    @Override
    public List<ParkingAreaResponse> convertAllToDto(List<ParkingArea> list) {
        return list.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
