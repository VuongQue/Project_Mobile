package com.example.s_parking.controller;

import com.example.s_parking.dto.request.PaymentRequest;
import com.example.s_parking.entity.Payment;
import com.example.s_parking.entity.Session;
import com.example.s_parking.repository.PaymentRepository;
import com.example.s_parking.repository.SessionRepository;
import com.example.s_parking.value.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    @Autowired
    private final PaymentRepository paymentRepository;

    @Autowired
    private final SessionRepository sessionRepository;

    /**
     * API tạo Payment mới và gán vào tất cả các Session chưa thanh toán của user đang đăng nhập
     */
    @PostMapping("/create-transaction")
    public ResponseEntity<String> createPayment(@RequestBody PaymentRequest request, Authentication authentication) {
        String username = authentication.getName(); // Lấy username từ Authentication

        // 1. Tạo Payment mới
        Payment payment = new Payment();
        payment.setAmount(request.getAmount());
        payment.setMethod(request.getMethod());
        payment.setStatus(PaymentStatus.UNPAID);
        payment.setTransactionId(generateTransactionId());
        payment.setCreatedAt(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);

        // 2. Gán Payment vào các Session chưa có payment
        List<Session> unpaidSessions = sessionRepository.findByUserUsernameAndPaymentIsNull(username);
        for (Session session : unpaidSessions) {
            session.setPayment(savedPayment);
        }
        sessionRepository.saveAll(unpaidSessions);

        return ResponseEntity.ok(savedPayment.getTransactionId());
    }

    /**
     * API xác nhận thanh toán (gửi transactionId để xác nhận PAID)
     */
    @PutMapping("/confirm")
    public ResponseEntity<String> confirmPayment(@RequestBody PaymentRequest request, Authentication authentication) {
        String username = authentication.getName(); // lấy username đăng nhập hiện tại

        Optional<Payment> optionalPayment = paymentRepository.findByTransactionId(request.getTransactionId());

        if (optionalPayment.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy giao dịch.");
        }

        Payment payment = optionalPayment.get();

        // Kiểm tra xem Payment này có thuộc các Session của user đang login không
        List<Session> sessions = sessionRepository.findByPayment(payment);

        boolean isUserOwner = sessions.stream()
                .allMatch(session -> session.getUser().getUsername().equals(username));

        if (!isUserOwner) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Bạn không có quyền xác nhận giao dịch này.");
        }

        // OK -> Đổi trạng thái PAID
        payment.setStatus(PaymentStatus.PAID);
        paymentRepository.save(payment);

        return ResponseEntity.ok("Đã xác nhận thanh toán thành công.");
    }


    /**
     * Hàm sinh mã giao dịch duy nhất
     */
    private String generateTransactionId() {
        return "TRANS_" + UUID.randomUUID().toString().replace("-", "").substring(0, 15).toUpperCase();
    }
}
