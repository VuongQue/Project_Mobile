package com.example.s_parking.controller;

import com.example.s_parking.dto.request.ConfirmPaymentRequest;
import com.example.s_parking.dto.request.PaymentRequest;
import com.example.s_parking.dto.response.PaymentResponse;
import com.example.s_parking.dto.response.SuccessResponse;
import com.example.s_parking.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-transaction")
    public ResponseEntity<?> createPayment(@RequestBody PaymentRequest request, Authentication authentication) {
        try {
            String username = authentication.getName();
            PaymentResponse paymentResponse = paymentService.createPayment(request, username);

            if (paymentResponse == null || paymentResponse.getTransactionId() == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Không thể tạo giao dịch mới!");
            }

            return ResponseEntity.ok(paymentResponse); // Trả trực tiếp PaymentResponse
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi: " + e.getMessage());
        }
    }

    @PutMapping(value = "/confirm", produces = "application/json")
    public ResponseEntity<SuccessResponse> confirmPayment(@RequestBody ConfirmPaymentRequest request, Authentication authentication) {
        try {
            String username = authentication.getName();
            String message = paymentService.confirmPayment(request, username);

            SuccessResponse response = new SuccessResponse(true, message);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(new SuccessResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new SuccessResponse(false, "Lỗi hệ thống: " + e.getMessage()));
        }
    }

}
