package com.example.s_parking.controller;

import com.example.s_parking.dto.request.PaymentRequest;
import com.example.s_parking.dto.response.ApiResponse;
import com.example.s_parking.entity.Payment;
import com.example.s_parking.entity.Session;
import com.example.s_parking.repository.PaymentRepository;
import com.example.s_parking.repository.SessionRepository;
import com.example.s_parking.value.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
     * API tạo Payment mới, chỉ lưu Payment, chưa gán vào session
     */
    @PostMapping("/create-transaction")
    public ResponseEntity<String> createPayment(@RequestBody PaymentRequest request, Authentication authentication) {
        Payment payment = new Payment();
        payment.setAmount(request.getAmount());
        payment.setMethod(request.getMethod());
        payment.setStatus(PaymentStatus.UNPAID);
        payment.setTransactionId(generateTransactionId());
        payment.setCreatedAt(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);

        return ResponseEntity.ok(savedPayment.getTransactionId());
    }




    /**
     * API xác nhận thanh toán thành công và gán Payment vào Session
     */
    @PutMapping("/confirm")
    public ResponseEntity<String> confirmPayment(@RequestBody PaymentRequest request, Authentication authentication) {
        String username = authentication.getName(); // username hiện tại

        Optional<Payment> optionalPayment = paymentRepository.findByTransactionId(request.getTransactionId());
        if (optionalPayment.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy giao dịch.");
        }

        Payment payment = optionalPayment.get();

        if (payment.getStatus() == PaymentStatus.PAID) {
            return ResponseEntity.badRequest().body("Giao dịch này đã được xác nhận trước đó.");
        }

        // Tìm tất cả Session chưa thanh toán (payment null) của user
        List<Session> unpaidSessions = sessionRepository.findByUserUsernameAndPaymentIsNull(username);

        if (unpaidSessions.isEmpty()) {
            return ResponseEntity.badRequest().body("Không có khoản nợ nào để xác nhận.");
        }

        // Gán payment vào các session chưa thanh toán
        for (Session session : unpaidSessions) {
            session.setPayment(payment);
        }
        sessionRepository.saveAll(unpaidSessions);

        // Cập nhật trạng thái Payment
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
