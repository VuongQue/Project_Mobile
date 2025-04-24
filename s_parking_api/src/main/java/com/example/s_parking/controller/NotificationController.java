package com.example.s_parking.controller;

import com.example.s_parking.dto.request.NotificationRequest;
import com.example.s_parking.dto.request.UsernameRequest;
import com.example.s_parking.dto.response.NotificationResponse;
import com.example.s_parking.dto.response.SuccessResponse;
import com.example.s_parking.entity.Notification;
import com.example.s_parking.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("notification")
public class NotificationController {
    @Autowired
    NotificationService notificationService;

    @PostMapping("/yours")
    public ResponseEntity<?> getMyNotification(@RequestBody UsernameRequest request, Authentication authentication) {
        String username = request.getUsername();
        String currentUser = authentication.getName();

        if (!username.equals(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Bạn không có quyền truy cập thông tin của người dùng khác!");
        }

        List<Notification> notificationList = notificationService.getByUsername(username);
        List<NotificationResponse> notificationResponseList = notificationService.convertAllToDto(notificationList);

        if (notificationResponseList == null || notificationResponseList.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Không có thông báo nào: " + username);
        }
        return ResponseEntity.ok(notificationResponseList);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateNotificationStatus(@RequestBody NotificationRequest request, Authentication authentication) {
        try {
            String username = request.getUsername();
            String currentUser = authentication.getName();

            if (!username.equals(currentUser)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Bạn không có quyền truy cập thông tin của người dùng khác!");
            }

            boolean isSaved = notificationService.updateNotificationService(request.getUsername(), request.getId());
            if (isSaved) {
                return ResponseEntity.ok(new SuccessResponse(true, "Update status successfully"));
            }
            return ResponseEntity.status(500).body(new SuccessResponse(false, "Failed to update this notification's status"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new SuccessResponse(false, "Error occurred: " + e.getMessage()));
        }
    }
}
