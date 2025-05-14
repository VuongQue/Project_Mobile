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

import java.util.Map;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Thanh toán qua Ngân hàng
     */
    @PostMapping("/create-transaction")
    public ResponseEntity<?> createPayment(@RequestBody PaymentRequest request, Authentication authentication) {
        try {
            String username = authentication.getName();
            PaymentResponse paymentResponse = paymentService.createPayment(request, username);

            if (paymentResponse == null || paymentResponse.getTransactionId() == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Không thể tạo giao dịch mới!");
            }

            return ResponseEntity.ok(paymentResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi: " + e.getMessage());
        }
    }

    /**
     * Xác nhận thanh toán ngân hàng
     */
    @PutMapping("/confirm")
    public ResponseEntity<SuccessResponse> confirmPayment(@RequestBody ConfirmPaymentRequest request, Authentication authentication) {
        try {
            String username = authentication.getName();
            String message = paymentService.confirmPayment(request, username);

            SuccessResponse response = new SuccessResponse(true, message);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SuccessResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new SuccessResponse(false, "Lỗi hệ thống: " + e.getMessage()));
        }
    }

    /**
     * Tạo giao dịch thanh toán qua MoMo
     */
    @PostMapping("/momo/create-transaction")
    public ResponseEntity<?> createMomoPayment(@RequestBody PaymentRequest request, Authentication authentication) {
        try {
            String username = authentication.getName();
            PaymentResponse response = paymentService.createMomoPayment(request, username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi MoMo: " + e.getMessage());
        }
    }


    /**
     * Xử lý callback từ MoMo
     */
    @PostMapping("/momo/notify")
    public ResponseEntity<String> handleMomoNotification(@RequestBody Map<String, Object> requestBody) {
        String orderId = (String) requestBody.get("orderId");
        int resultCode = (int) requestBody.get("resultCode");
        String transId = (String) requestBody.get("transId");

        if (resultCode == 0) {
            // Thanh toán thành công, cập nhật trạng thái đơn hàng
            paymentService.updatePaymentStatus(orderId, "PAID");
            return ResponseEntity.ok("Success");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment failed");
        }
    }

}
