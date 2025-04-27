package com.example.s_parking.controller;

import com.example.s_parking.dto.request.PaymentRequest;
import com.example.s_parking.dto.response.SessionResponse;
import com.example.s_parking.entity.Payment;
import com.example.s_parking.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    // API tạo giao dịch thanh toán
    @PostMapping("/create-transaction")
    public ResponseEntity<String> createTransaction(@RequestBody List<SessionResponse> responses) {
        double totalAmount = 0;
        String transactionId = UUID.randomUUID().toString(); // Tạo mã giao dịch duy nhất

        // Tính tổng số tiền từ các lượt gửi xe đã chọn
        for (SessionResponse session : responses) {
            totalAmount += session.getFee();
        }

        // Tạo một giao dịch thanh toán mới
        Payment payment = new Payment();
        payment.setTransactionId(transactionId);
        payment.setAmount(totalAmount);
        payment.setStatus("PENDING"); // Giao dịch ở trạng thái PENDING
        payment.setCreatedAt(LocalDateTime.now());
        payment.setMethod("BANK_TRANSFER"); // Phương thức thanh toán qua ngân hàng

        // Lưu giao dịch vào cơ sở dữ liệu
        paymentRepository.save(payment);

        return ResponseEntity.ok("Giao dịch đã được tạo với mã giao dịch: " + transactionId);
    }

    // API xác nhận thanh toán
    @PutMapping("/confirm")
    public ResponseEntity<String> confirmPayment(@RequestBody PaymentRequest paymentRequest) {
        // Kiểm tra giao dịch tồn tại
        Payment payment = paymentRepository.findByTransactionId(paymentRequest.getTransactionId());
        if (payment != null && payment.getStatus().equals("PENDING")) {
            payment.setStatus("SUCCESS");  // Cập nhật trạng thái
            paymentRepository.save(payment);  // Lưu vào database

            return ResponseEntity.ok("Thanh toán đã được xác nhận.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Giao dịch không tồn tại.");
        }
    }
}
