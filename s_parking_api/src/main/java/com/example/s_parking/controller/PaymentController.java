package com.example.s_parking.controller;

import com.example.s_parking.dto.request.ConfirmPaymentRequest;
import com.example.s_parking.dto.request.PaymentRequest;
import com.example.s_parking.dto.response.PaymentResponse;
import com.example.s_parking.dto.response.SuccessResponse;
import com.example.s_parking.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    @Value("${momo.secretKey}")
    private String secretKey;

    /**
     * Tạo giao dịch qua Ngân hàng
     */
    @PostMapping("/create-transaction")
    public ResponseEntity<?> createPayment(@RequestBody PaymentRequest request, Authentication authentication) {
        try {
            String username = authentication.getName();
            PaymentResponse paymentResponse = paymentService.createPayment(request, username);

            if (paymentResponse == null || paymentResponse.getTransactionId() == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không thể tạo giao dịch mới!");
            }

            return ResponseEntity.ok(paymentResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi: " + e.getMessage());
        }
    }

    /**
     * Tạo giao dịch qua MoMo
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
        try {
            System.out.println("MoMo Notification Received: " + requestBody);

            // Kiểm tra chữ ký
            if (!validateSignature(requestBody)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Signature không hợp lệ");
            }

            String orderId = String.valueOf(requestBody.get("orderId"));
            Integer resultCode = Integer.parseInt(String.valueOf(requestBody.get("resultCode")));
            String message = String.valueOf(requestBody.get("message"));

            if (resultCode == 0) {
                paymentService.updatePaymentStatus(orderId, "PAID");
                return ResponseEntity.ok("Success");
            } else {
                paymentService.updatePaymentStatus(orderId, "FAILED");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment failed: " + message);
            }

        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data parsing error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: " + e.getMessage());
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

            return ResponseEntity.ok(new SuccessResponse(true, message));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SuccessResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new SuccessResponse(false, "Lỗi hệ thống: " + e.getMessage()));
        }
    }

    /**
     * Kiểm tra chữ ký từ MoMo
     */
    private boolean validateSignature(Map<String, Object> requestBody) {
        try {
            String partnerCode = String.valueOf(requestBody.get("partnerCode"));
            String orderId = String.valueOf(requestBody.get("orderId"));
            String requestId = String.valueOf(requestBody.get("requestId"));
            String amount = String.valueOf(requestBody.get("amount"));
            String resultCode = String.valueOf(requestBody.get("resultCode"));
            String message = String.valueOf(requestBody.get("message"));
            String payType = String.valueOf(requestBody.get("payType"));
            String transId = String.valueOf(requestBody.get("transId"));
            String responseTime = String.valueOf(requestBody.get("responseTime"));
            String signature = String.valueOf(requestBody.get("signature"));

            String rawData = "amount=" + amount +
                    "&extraData=" +
                    "&message=" + message +
                    "&orderId=" + orderId +
                    "&orderInfo=" + requestBody.get("orderInfo") +
                    "&orderType=momo_wallet" +
                    "&partnerCode=" + partnerCode +
                    "&payType=" + payType +
                    "&requestId=" + requestId +
                    "&responseTime=" + responseTime +
                    "&resultCode=" + resultCode +
                    "&transId=" + transId;

            String generatedSignature = generateSignature(rawData);

            System.out.println("Generated Signature: " + generatedSignature);
            System.out.println("Received Signature: " + signature);

            return generatedSignature.equals(signature);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Tạo chữ ký HMAC SHA256
     */
    private String generateSignature(String data) throws Exception {
        Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmacSHA256.init(secretKeySpec);
        byte[] hash = hmacSHA256.doFinal(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }
}
