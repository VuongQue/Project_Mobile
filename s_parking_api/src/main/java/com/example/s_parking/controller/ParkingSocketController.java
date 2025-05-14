package com.example.s_parking.controller;

import com.example.s_parking.dto.response.MyCurrentSessionResponse;
import com.example.s_parking.dto.response.NotificationResponse;
import com.example.s_parking.dto.response.ParkingAreaResponse;
import com.example.s_parking.entity.ParkingArea;
import com.example.s_parking.service.ParkingAreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ParkingSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    ParkingAreaService parkingAreaService;

    public void updateSlots() {
        List<ParkingArea> parkingAreas = parkingAreaService.getParkingAreas();
        List<ParkingAreaResponse> parkingAreasResponse = parkingAreaService.convertAllToDto(parkingAreas);
        messagingTemplate.convertAndSend("/topic/parkingArea", parkingAreasResponse);
    }

    public void sendCheckInOutNotification(String username, MyCurrentSessionResponse response) {
        messagingTemplate.convertAndSend("/topic/checkin/" + username, response);
    }

    public void sendUserNotification(String username, NotificationResponse notification) {
        messagingTemplate.convertAndSend("/topic/notification/" + username, notification);
    }

}

