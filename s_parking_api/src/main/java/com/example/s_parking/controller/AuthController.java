package com.example.s_parking.controller;

import com.example.s_parking.dto.request.*;
import com.example.s_parking.entity.User;
import com.example.s_parking.service.JwtService;
import com.example.s_parking.service.UserService;
import com.example.s_parking.service.OTPService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final OTPService otpService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            String role = auth.getAuthorities().iterator().next().getAuthority();
            String token = jwtService.generateToken(auth.getName(), role);

            return ResponseEntity.ok(Map.of("token", token));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai tài khoản hoặc mật khẩu");
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        boolean success = userService.register(request.getUsername(), request.getPassword(), request.getRole());

        if (success) {
            return ResponseEntity.ok("Đăng ký thành công");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tài khoản đã tồn tại");
        }
    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody UsernameRequest request) {
        Optional<User> userOpt = userService.getUserInfo(request.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy tài khoản");
        }

        otpService.sendOTP(userOpt.get(), "ACTIVATE");
        return ResponseEntity.ok("Đã gửi OTP về email của bạn");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OTPRequest request) {
        Optional<User> userOpt = userService.getUserInfo(request.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy tài khoản");
        }

        boolean verified = otpService.verifyOTP(userOpt.get(), request.getOTP(), "ACTIVATE");
        if (verified) {
            return ResponseEntity.ok("Xác thực OTP thành công");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mã OTP không hợp lệ hoặc đã hết hạn");
        }
    }

    @PostMapping("/update-info")
    public ResponseEntity<?> updateInfo(@RequestBody UpdateInfoRequest request) {
        Optional<User> userOpt = userService.getUserInfo(request.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy tài khoản");
        }

        try {
            boolean updated = userService.updateUserInfo(userOpt.get(), request);
            if (updated) {
                return ResponseEntity.ok("Cập nhật thông tin thành công");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không thể cập nhật thông tin");
            }
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
    }
}
