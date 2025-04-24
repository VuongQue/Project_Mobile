package com.example.s_parking.implement;

import com.example.s_parking.dto.response.ParkingLotResponse;
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
    public ParkingLotResponse convertToDto(ParkingLot entity) {
        return ParkingLotResponse.builder()
                .id(entity.getId())
                .row(entity.getRow())
                .pos(entity.getPos())
                .status(entity.getStatus())
                .area(entity.getArea().getIdArea())
                .build();
    }

    @Override
    public List<ParkingLotResponse> convertAllToDto(List<ParkingLot> list) {
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
    public ParkingLot updateParkingLot(ParkingLot parkingLot) {
        try {
            if (parkingLotRepository.existsById(parkingLot.getId())) {
                return parkingLotRepository.save(parkingLot);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteParkingLot(Long id) {

    }

    @Override
    public Optional<ParkingLot> getSlot() {
        return parkingLotRepository.findFirstByStatus("Available");
    }
}
