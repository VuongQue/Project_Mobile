package com.example.s_parking.controller;

import com.example.s_parking.dto.request.InOutRequest;
import com.example.s_parking.dto.request.UsernameRequest;
import com.example.s_parking.dto.response.MyCurrentSessionResponse;
import com.example.s_parking.dto.response.NotificationResponse;
import com.example.s_parking.dto.response.SessionResponse;
import com.example.s_parking.entity.*;
import com.example.s_parking.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController

@RequestMapping("/sessions")
@RequiredArgsConstructor
public class SessionController {

    @Autowired
    private final SessionService sessionService;

    @Autowired
    private final UserService userService;

    @Autowired
    private final BookingService bookingService;

    @Autowired
    private final ParkingLotService parkingLotService;

    @Autowired
    private ParkingSocketController parkingSocketController;

    @Autowired
    private NotificationService notificationService;

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

    @PostMapping("/my-current-session")
    public ResponseEntity<?> getMyCurrentSession(@RequestBody UsernameRequest request, Authentication authentication) {
        String username = request.getUsername();
        String currentUser = authentication.getName(); // username đăng nhập hiện tại

        if (!username.equals(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Bạn không có quyền truy cập thông tin của người dùng khác!");
        }
        Session session = sessionService.getMyCurrentSession(username);
        MyCurrentSessionResponse myCurrentSessionResponse = sessionService.convertToDTO(session);
        if (myCurrentSessionResponse == null ) {
            myCurrentSessionResponse = new MyCurrentSessionResponse();
        }
        return ResponseEntity.ok(myCurrentSessionResponse);
    }

    @PostMapping("/check-in")
    public ResponseEntity<?> checkIn(@RequestBody InOutRequest request) {
        String[] parts = request.getCode().split("-");
        String username = parts[0];
        String key = parts[1];

        Optional<User> user = userService.findByUsername(username);
        if (user.get().getSecurity_key() == null || user.get().getSecurity_key().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Không có người dùng này");
        }

        if (!user.get().getSecurity_key().equals(key)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body("Khóa không đúng");
        }

        Optional<Booking> booking = bookingService.findByUsernameAndDate(username.trim(), LocalDate.now());
        Session session = null;
        if (booking.isPresent()) {
            session = new Session(null, LocalDateTime.now(), null, request.getLicensePlate(), "Booked", 0,
                    booking.get().getUser(), booking.get().getParking(), booking.get().getPayment());
        }
        else {
            Optional<ParkingLot> optionalSlot  = parkingLotService.getSlot();
            if (optionalSlot.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body("Hết chỗ");
            }

            session = new Session(null, LocalDateTime.now(), null, request.getLicensePlate(), "Normal", 4000,
                    user.get(), optionalSlot.get(), null);
            optionalSlot.get().setStatus("Unavailable");
            parkingLotService.updateParkingLot(optionalSlot.get().getId(), optionalSlot.get());
        }
        if (session == null)
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body("Có lỗi");

        sessionService.createSession(session);
        MyCurrentSessionResponse currentSessionResponse = sessionService.convertToDTO(session);

        Notification notification = new Notification(null, "Check In", "Xe của bạn đã được đổ ở" + session.getParking().getLocation(), LocalDateTime.now());
        NotificationResponse notificationResponse = notificationService.convertToDto(notification);

        parkingSocketController.sendCheckInNotification(username, currentSessionResponse);
        parkingSocketController.sendUserNotification(username, notificationResponse);

        return ResponseEntity.status(HttpStatus.OK)
                .body("Checked In");
    }
}
