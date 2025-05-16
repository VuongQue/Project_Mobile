package com.example.s_parking.controller;

import com.example.s_parking.dto.request.BookingRequest;
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
import java.util.Optional;
import java.util.stream.Collectors;

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

        System.out.println("bookingResponseList: " + bookingList.size());

        if (bookingResponseList == null || bookingResponseList.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Không tìm dữ liệu");
        }
        return ResponseEntity.ok(bookingResponseList);
    }
    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request, Authentication authentication) {
        String username = authentication.getName();
        request.setUsername(username);

        BookingResponse response = bookingService.createBooking(request);

        if (response == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Booking không thành công");
        }
        return ResponseEntity.ok(response);
    }

    /**
     * API lấy tất cả các booking chưa thanh toán
     */
    @PostMapping("/unpaid")
    public ResponseEntity<List<BookingResponse>> getUnpaidBookings(@RequestBody UsernameRequest request) {
        List<Booking> bookings = bookingService.getUnpaidBookings(request.getUsername());
        List<BookingResponse> bookingResponses = bookings.stream()
                .map(bookingService::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(bookingResponses);
    }

    /**
     * API lấy thông tin booking cụ thể theo ID
     */
    @PostMapping("/get")
    public ResponseEntity<BookingResponse> getBookingById(@RequestBody BookingRequest request) {
        Optional<Booking> booking = bookingService.getBookingById(request.getId());
        return booking.map(value -> ResponseEntity.ok(bookingService.convertToDto(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
