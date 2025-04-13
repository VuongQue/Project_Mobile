package com.example.s_parking.controller;

import com.example.s_parking.dto.response.SessionBasicInfo;
import com.example.s_parking.dto.request.UsernameRequest;
import com.example.s_parking.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

@RequestMapping("/user/sessions")
@RequiredArgsConstructor
public class SessionController {

    @Autowired
    private final SessionService sessionService;

    @PostMapping
    @PreAuthorize("#username == authentication.name")
    public ResponseEntity<?> getSessionsByUsername(@RequestBody UsernameRequest request) {
        String username = request.getUsername();
        List<SessionBasicInfo> sessions = sessionService.getSessionsByUsername(username);

        if (sessions == null || sessions.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy phiên làm việc nào cho người dùng: " + username);
        }

        return ResponseEntity.ok(sessions);
    }

}
