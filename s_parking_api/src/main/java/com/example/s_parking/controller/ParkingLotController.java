package com.example.s_parking.controller;

import com.example.s_parking.dto.request.ParkingLotRequest;
import com.example.s_parking.entity.ParkingLot;
import com.example.s_parking.repository.ParkingLotRepository;
import com.example.s_parking.service.ParkingLotService;
import com.example.s_parking.utils.Specifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/parking-lots")
public class ParkingLotController {
    @Autowired
    ParkingLotService parkingLotService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllParkingLots() {
        List<ParkingLot> parkingLots = parkingLotService.getAllParkingLots();
        List<ParkingLotRequest> parkingLotRequests = parkingLotService.convertAllToDto(parkingLots);
        return ResponseEntity.ok(parkingLotRequests);
    }

    /*@PostMapping("/filter")
    public ResponseEntity<?> filterParkingLots(@RequestBody ParkingLotRequest request) {
        List<ParkingLotRequest> filteredLots = parkingLotRepository.findAll(Specifications.ParkingLotFilterBy(request));
        return ResponseEntity.ok(filteredLots);
    }*/
}
