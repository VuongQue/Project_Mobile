package com.example.s_parking.service;

import com.example.s_parking.dto.response.NotificationResponse;
import com.example.s_parking.entity.Notification;

public interface NotificationService {
    NotificationResponse convertToDto(Notification entity);
}
