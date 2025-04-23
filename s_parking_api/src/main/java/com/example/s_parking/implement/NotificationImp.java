package com.example.s_parking.implement;

import com.example.s_parking.dto.response.NotificationResponse;
import com.example.s_parking.entity.Notification;
import com.example.s_parking.repository.NotificationRepository;
import com.example.s_parking.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationImp implements NotificationService {

    @Autowired
    NotificationRepository notificationRepository;

    @Override
    public void createNewNotificatioin(Notification notification) {
        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationResponse> convertAllToDto(List<Notification> list) {
        return list.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public NotificationResponse convertToDto(Notification entity) {
        return NotificationResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .createdAt(entity.getCreatedAt())
                .isRead(entity.isRead())
                .build();
    }

    @Override
    public List<Notification> getByUsername(String username) {
        return notificationRepository.findByUserUsernameOrderByCreatedAtDesc(username);
    }

    @Override
    public boolean updateNotificationService(String username, Long id) {
        return notificationRepository.updateStatusByIdAndUserUsername(id, username) > 0;
    }
}
