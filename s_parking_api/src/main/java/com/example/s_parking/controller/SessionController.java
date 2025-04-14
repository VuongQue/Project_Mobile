package com.example.s_parking.controller;

import com.example.s_parking.dto.request.UsernameRequest;
import com.example.s_parking.dto.response.SessionResponse;
import com.example.s_parking.entity.Session;
import com.example.s_parking.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController

@RequestMapping("/sessions")
@RequiredArgsConstructor
public class SessionController {

    @Autowired
    private final SessionService sessionService;

    @PostMapping("/user")
    public ResponseEntity<?> getSessionsByUsername(@RequestBody UsernameRequest request, Authentication authentication) {

        String username = request.getUsername();
        String currentUser = authentication.getName(); // username đăng nhập hiện tại

        if (!username.equals(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Bạn không có quyền truy cập thông tin của người dùng khác!");
        }

        List<Session> sessions = sessionService.getSessionByUsername(username);
        List<SessionResponse> sessionResponses = sessionService.convertAllToDto(sessions);
        if (sessionResponses == null || sessionResponses.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy phiên làm việc nào cho người dùng: " + username);
        }
        return ResponseEntity.ok(sessionResponses);
    }
}
