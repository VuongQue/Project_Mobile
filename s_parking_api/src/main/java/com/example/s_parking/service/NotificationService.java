package com.example.s_parking.service;

import com.example.s_parking.dto.response.NotificationResponse;
import com.example.s_parking.entity.Notification;

import java.util.List;

public interface NotificationService {
    void createNewNotificatioin(Notification notification);

    List<NotificationResponse> convertAllToDto(List<Notification> list);

    NotificationResponse convertToDto(Notification entity);

    List<Notification> getByUsername(String username);

    boolean updateNotificationService(String username, Long id);
}
