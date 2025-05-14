package com.example.s_parking.controller;

import com.example.s_parking.dto.request.InOutRequest;
import com.example.s_parking.dto.request.UsernameRequest;
import com.example.s_parking.dto.response.MyCurrentSessionResponse;
import com.example.s_parking.dto.response.NotificationResponse;
import com.example.s_parking.dto.response.SessionResponse;
import com.example.s_parking.entity.*;
import com.example.s_parking.service.*;
import com.example.s_parking.value.ParkingStatus;
import com.example.s_parking.value.SessionType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.*;
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
    private final ParkingAreaService parkingAreaService;

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

    @PostMapping("/check-in-out")
    public ResponseEntity<?> checkInOut(@RequestBody InOutRequest request) {

        String username = request.getUsername();
        String licensePlate = request.getLicensePlate();

        Optional<User> user = userService.findByUsername(username);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Không có người dùng này");
        }

        if (!user.get().getLicensePlate().equals(licensePlate)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body("Không phải xe của người dùng này");
        }

        Notification notification;
        MyCurrentSessionResponse currentSessionResponse;

        // Kiểm tra là check in hay check out
        Session lastSession;
        Session session;
        lastSession = sessionService.getMyCurrentSession(user.get().getUsername());
        if (lastSession == null || lastSession.getCheckOut() != null) {
            // check in
            // Nếu có đặt trước
            Optional<Booking> booking = bookingService.findByUsernameAndDate(username.trim(), LocalDate.now());
            if (booking.isPresent()) {
                session = new Session(null, LocalDateTime.now(), null, licensePlate, SessionType.RESERVED, 0,
                        booking.get().getUser(), booking.get().getParking(), booking.get().getPayment());
            }
            // Nếu không đặt trước
            else {
                Optional<ParkingLot> optionalSlot  = parkingLotService.getSlot();
                if (optionalSlot.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                            .body("Hết chỗ");
                }

                session = new Session(null, LocalDateTime.now(), null, licensePlate, SessionType.NOT_RESERVED, 0,
                        user.get(), optionalSlot.get(), null);
                optionalSlot.get().setStatus(ParkingStatus.UNAVAILABLE);
                parkingLotService.updateParkingLot(optionalSlot.get());
            }
            // thao tác với csdl và thông báo
            sessionService.createSession(session);
            notification = new Notification(null, "Check In", "Xe của bạn đã được đổ ở: " + session.getParking().getLocation(), LocalDateTime.now(), false, user.get());

        }
        else {
            //check out
            // Nếu có đặt trước
            if (lastSession.getType().equals(SessionType.RESERVED)) {
                lastSession.setCheckOut(LocalDateTime.now());
            }
            // Nếu không có đặt trước
            else {
                lastSession.setCheckOut(LocalDateTime.now());
                lastSession.setFee(calculateParkingFee(lastSession.getCheckIn(), lastSession.getCheckOut()));
                ParkingLot parkingLot = lastSession.getParking();
                parkingLot.setStatus(ParkingStatus.AVAILABLE);
                parkingLotService.updateParkingLot(parkingLot);
            }
            // thao tác với csdl và thông báo
            sessionService.updateSession(lastSession);
            notification = new Notification(null, "Check Out", "Xe của bạn đã được lấy thành công", LocalDateTime.now(), false, user.get());

        }

        parkingAreaService.updateSlots();
        currentSessionResponse = sessionService.convertToDTO(lastSession);

        notificationService.createNewNotificatioin(notification);
        NotificationResponse notificationResponse = notificationService.convertToDto(notification);

        parkingSocketController.sendCheckInOutNotification(username, currentSessionResponse);
        parkingSocketController.sendUserNotification(username, notificationResponse);

        return ResponseEntity.status(HttpStatus.OK)
                .body(notificationResponse.getTitle());
    }

    public float calculateParkingFee(LocalDateTime checkIn, LocalDateTime checkOut) {
        // Nếu quá 21h cùng ngày → 50.000đ
        if (checkOut.toLocalTime().isAfter(LocalTime.of(21, 0)) &&
                checkIn.toLocalDate().equals(checkOut.toLocalDate())) {
            return 50000;
        }

        // Nếu gửi > 12 tiếng → 9.000đ
        long durationInMinutes = Duration.between(checkIn, checkOut).toMinutes();
        if (durationInMinutes > 12 * 60) {
            return 9000;
        }

        // Nếu gửi vào Chủ nhật → 5.000đ
        if (checkIn.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return 5000;
        }

        // Từ Thứ 2 → Thứ 7
        if (checkOut.toLocalTime().isAfter(LocalTime.of(18, 30))) {
            return 5_000;
        } else {
            return 4_000;
        }
    }
    @PostMapping("/unpaid")
    public ResponseEntity<?> getUnpaidSessions(@RequestBody UsernameRequest request, Authentication authentication) {
        String username = request.getUsername();
        String currentUser = authentication.getName(); // username đăng nhập

        if (!username.equals(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Bạn không có quyền truy cập thông tin của người dùng khác!");
        }

        List<Session> unpaidSessions = sessionService.getUnpaidSessions(username);
        List<SessionResponse> responseList = sessionService.convertAllToDto(unpaidSessions);

        if (responseList == null || responseList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy phiên gửi xe chưa thanh toán nào.");
        }

        return ResponseEntity.ok(responseList);
    }


}
