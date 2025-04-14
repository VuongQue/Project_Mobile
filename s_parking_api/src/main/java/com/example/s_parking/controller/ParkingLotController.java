package com.example.s_parking.controller;

import com.example.s_parking.dto.response.ParkingLotResponse;
import com.example.s_parking.entity.ParkingLot;
import com.example.s_parking.service.ParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        List<ParkingLotResponse> parkingLotResponses = parkingLotService.convertAllToDto(parkingLots);
        if (parkingLotResponses == null || parkingLotResponses.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Không tìm dữ liệu");
        }
        return ResponseEntity.ok(parkingLotResponses);
    }

    /*@PostMapping("/filter")
    public ResponseEntity<?> filterParkingLots(@RequestBody ParkingLotRequest request) {
        List<ParkingLotRequest> filteredLots = parkingLotRepository.findAll(Specifications.ParkingLotFilterBy(request));
        return ResponseEntity.ok(filteredLots);
    }*/
}
