package com.example.s_parking.controller;

import com.example.s_parking.dto.request.UsernameRequest;
import com.example.s_parking.dto.response.UserInfoResponse;
import com.example.s_parking.entity.User;
import com.example.s_parking.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/profile")
    public ResponseEntity<?> getUserInfo(@RequestBody UsernameRequest request, Authentication authentication) {

        String username = request.getUsername();
        String currentUser = authentication.getName(); // username đăng nhập hiện tại

        if (!username.equals(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Bạn không có quyền truy cập thông tin của người dùng khác!");
        }

        Optional<User> user = userService.getUserInfo(username);
        UserInfoResponse userInfoResponse = userService.convertToDto(user.orElse(null));
        if (userInfoResponse == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy thông tin của người dùng: " + username);
        }
        return ResponseEntity.ok(userInfoResponse);
    }


}
