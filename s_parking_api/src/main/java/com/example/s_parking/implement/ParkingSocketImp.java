package com.example.s_parking.implement;

import com.example.s_parking.dto.response.MyCurrentSessionResponse;
import com.example.s_parking.dto.response.NotificationResponse;
import com.example.s_parking.dto.response.ParkingAreaResponse;
import com.example.s_parking.entity.ParkingArea;
import com.example.s_parking.service.ParkingAreaService;
import com.example.s_parking.service.ParkingSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ParkingSocketImp implements ParkingSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    ParkingAreaService parkingAreaService;

    @Override
    public void updateSlots() {

        List<ParkingArea> parkingAreas = parkingAreaService.getParkingAreas();
        System.out.println(parkingAreas.get(0).getAvailableSlots());
        List<ParkingAreaResponse> parkingAreasResponse = parkingAreaService.convertAllToDto(parkingAreas);
        messagingTemplate.convertAndSend("/topic/parkingArea", parkingAreasResponse);
    }


    @Override
    public void sendCheckInOutNotification(String username, MyCurrentSessionResponse response) {
        messagingTemplate.convertAndSend("/topic/checkin/" + username, response);
    }

    @Override
    public void sendUserNotification(String username, NotificationResponse notification) {
        messagingTemplate.convertAndSend("/topic/notification/" + username, notification);
    }

}

