package com.example.s_parking.controller;

import com.example.s_parking.dto.request.InOutRequest;
import com.example.s_parking.dto.request.UsernameRequest;
import com.example.s_parking.dto.response.MyCurrentSessionResponse;
import com.example.s_parking.dto.response.NotificationResponse;
import com.example.s_parking.dto.response.SessionResponse;
import com.example.s_parking.entity.*;
import com.example.s_parking.service.*;
import com.example.s_parking.utils.CalculateUtil;
import com.example.s_parking.value.ParkingStatus;
import com.example.s_parking.value.SessionType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
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
    private ParkingSocketService parkingSocketService;

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
        MyCurrentSessionResponse myCurrentSessionResponse = sessionService.convertToMyDto(session);
        if (myCurrentSessionResponse == null ) {
            myCurrentSessionResponse = new MyCurrentSessionResponse();
        }
        return ResponseEntity.ok(myCurrentSessionResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/check-in-out")
    @Transactional
    public ResponseEntity<?> checkInOut(@RequestBody InOutRequest request, Authentication authentication) {

        String username = request.getUsername();
        String licensePlate = request.getLicensePlate();

        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không có người dùng này");
        }

        User user = userOpt.get();
        if (!user.getLicensePlate().equals(licensePlate)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Không phải xe của người dùng này");
        }

        Optional<Session> lastSessionOpt = Optional.ofNullable(sessionService.getMyCurrentSession(username));
        boolean isCheckIn = lastSessionOpt.isEmpty() || lastSessionOpt.get().getCheckOut() != null;

        Notification notification;
        ResponseEntity<?> response;

        if (isCheckIn) {
            response = handleCheckIn(username, licensePlate, user);
            if (response.getStatusCode() == HttpStatus.NOT_ACCEPTABLE) {
                return response;
            }

            Session session = (Session) response.getBody();
            notification = new Notification(null, "Check In", "Xe của bạn đã được đổ ở: " + session.getParking().getLocation(),
                    LocalDateTime.now(), false, user);

        } else {
            Session lastSession = lastSessionOpt.get();
            handleCheckOut(lastSession);
            notification = new Notification(null, "Check Out", "Xe của bạn đã được lấy thành công",
                    LocalDateTime.now(), false, user);
        }

        notificationService.createNewNotificatioin(notification);
        NotificationResponse notificationResponse = notificationService.convertToDto(notification);

        Session myCurrentSession = sessionService.getMyCurrentSession(username);
        MyCurrentSessionResponse myCurrentSessionResponse = sessionService.convertToMyDto(myCurrentSession);

        parkingAreaService.updateSlots();

        parkingSocketService.sendUserNotification(username, notificationResponse);
        parkingSocketService.sendCheckInOutNotification(username, myCurrentSessionResponse);

        return ResponseEntity.ok(notificationResponse.getTitle());
    }

    /**
     * Xử lý Check In
     */
    private ResponseEntity<?> handleCheckIn(String username, String licensePlate, User user) {
        Optional<Booking> bookingOpt = bookingService.findByUsernameAndDate(username.trim(), LocalDate.now());
        Session session;

        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            session = new Session(null, LocalDateTime.now(), null, licensePlate, SessionType.RESERVED, 0,
                    booking.getUser(), booking.getParking(), booking.getPayment());
        } else {
            Optional<ParkingLot> optionalSlot = parkingLotService.getSlot();
            if (optionalSlot.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Hết chỗ");
            }

            ParkingLot slot = optionalSlot.get();
            slot.setStatus(ParkingStatus.UNAVAILABLE);
            parkingLotService.updateParkingLot(slot);

            session = new Session(null, LocalDateTime.now(), null, licensePlate, SessionType.NOT_RESERVED, 0,
                    user, slot, null);
        }

        sessionService.createSession(session);
        return ResponseEntity.ok(session);
    }

    private void handleCheckOut(Session lastSession) {
        lastSession.setCheckOut(LocalDateTime.now());

        if (lastSession.getType().equals(SessionType.NOT_RESERVED)) {
            float fee = CalculateUtil.calculateParkingFee(lastSession.getCheckIn(), lastSession.getCheckOut());
            lastSession.setFee( fee);

            ParkingLot parkingLot = lastSession.getParking();
            parkingLot.setStatus(ParkingStatus.AVAILABLE);
            parkingLotService.updateParkingLot(parkingLot);
        }

        sessionService.updateSession(lastSession);
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
