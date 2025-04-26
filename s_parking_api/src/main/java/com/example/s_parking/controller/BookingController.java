package com.example.s_parking.controller;

import com.example.s_parking.dto.request.UsernameRequest;
import com.example.s_parking.dto.response.BookingResponse;
import com.example.s_parking.entity.Booking;
import com.example.s_parking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/my-booking-history")
    public ResponseEntity<?> getMyBookingHistory(@RequestBody UsernameRequest request, Authentication authentication) {
        String username = request.getUsername();
        String currentUser = authentication.getName(); // username đăng nhập hiện tại

        if (!username.equals(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Bạn không có quyền truy cập thông tin của người dùng khác!");
        }

        List<Booking> bookingList = bookingService.getBookingByUserUsername(username);
        List<BookingResponse> bookingResponseList = bookingService.convertAllToDto(bookingList);

        if (bookingResponseList == null || bookingResponseList.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Không tìm dữ liệu");
        }
        return ResponseEntity.ok(bookingResponseList);
    }
}
