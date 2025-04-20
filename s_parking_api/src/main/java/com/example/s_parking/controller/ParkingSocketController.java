package com.example.s_parking.controller;

import com.example.s_parking.dto.response.MyCurrentSessionResponse;
import com.example.s_parking.dto.response.NotificationResponse;
import com.example.s_parking.dto.response.ParkingAreaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ParkingSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/api/parking/broadcast")
    public void updateSlots(@RequestBody ParkingAreaResponse response) {
        // Gửi tới tất cả client đang subscribe
        messagingTemplate.convertAndSend("/topic/parkingArea", response);
    }

    public void sendCheckInNotification(String username, MyCurrentSessionResponse response) {
        messagingTemplate.convertAndSend("/topic/checkin/" + username, response);
    }

    public void sendUserNotification(String username, NotificationResponse notification) {
        messagingTemplate.convertAndSend("/topic/notification/" + username, notification);
    }

}

