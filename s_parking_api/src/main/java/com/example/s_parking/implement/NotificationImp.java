package com.example.s_parking.implement;

import com.example.s_parking.dto.response.NotificationResponse;
import com.example.s_parking.entity.Notification;
import com.example.s_parking.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
public class NotificationImp implements NotificationService {
    @Override
    public NotificationResponse convertToDto(Notification entity) {
        return NotificationResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
