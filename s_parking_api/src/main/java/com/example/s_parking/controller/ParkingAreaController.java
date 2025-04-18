package com.example.s_parking.controller;

import com.example.s_parking.dto.response.ParkingAreaResponse;
import com.example.s_parking.entity.ParkingArea;
import com.example.s_parking.service.ParkingAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("parking-area")
public class ParkingAreaController {
    @Autowired
    ParkingAreaService parkingAreaService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllParkingAreas() {
        List<ParkingArea> parkingAreas = parkingAreaService.getParkingAreas();
        List<ParkingAreaResponse> parkingAreaResponses = parkingAreaService.convertAllToDto(parkingAreas);
        if (parkingAreaResponses == null || parkingAreaResponses.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy dữ liệu");
        }
        return ResponseEntity.ok(parkingAreaResponses);
    }
}
