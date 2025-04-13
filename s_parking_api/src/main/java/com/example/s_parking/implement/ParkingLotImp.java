package com.example.s_parking.implement;

import com.example.s_parking.dto.request.ParkingLotRequest;
import com.example.s_parking.entity.ParkingLot;
import com.example.s_parking.repository.ParkingLotRepository;
import com.example.s_parking.service.ParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ParkingLotImp implements ParkingLotService {
    @Autowired
    ParkingLotRepository parkingLotRepository;
    @Override
    public List<ParkingLot> getAllParkingLots() {
        return parkingLotRepository.findAll();
    }

    @Override
    public ParkingLotRequest convertToDto(ParkingLot entity) {
        return ParkingLotRequest.builder()
                .id(entity.getId())
                .row(entity.getRow())
                .pos(entity.getPos())
                .status(entity.getStatus())
                .area(entity.getArea().getIdArea())
                .build();
    }

    @Override
    public List<ParkingLotRequest> convertAllToDto(List<ParkingLot> list) {
        return list.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ParkingLot> getParkingLotById(Long id) {
        return Optional.empty();
    }

    @Override
    public ParkingLot createParkingLot(ParkingLot booking) {
        return null;
    }

    @Override
    public ParkingLot updateParkingLot(Long id, ParkingLot parkingLot) {
        return null;
    }

    @Override
    public void deleteParkingLot(Long id) {

    }
}
