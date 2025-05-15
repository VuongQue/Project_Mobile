package com.example.s_parking.service;

import com.example.s_parking.dto.response.MyCurrentSessionResponse;
import com.example.s_parking.dto.response.NotificationResponse;
import com.example.s_parking.dto.response.ParkingAreaResponse;

public interface ParkingSocketService {

    void updateSlots();

    void sendCheckInOutNotification(String username, MyCurrentSessionResponse response);

    void sendUserNotification(String username, NotificationResponse notification);
}
