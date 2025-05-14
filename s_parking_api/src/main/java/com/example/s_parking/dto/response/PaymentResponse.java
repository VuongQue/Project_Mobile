package com.example.s_parking.dto.response;

import com.example.s_parking.value.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String transactionId;
    private double amount;
    private String method;
    private String status;
    private LocalDateTime createdAt;
    private String payUrl;  // Đường dẫn thanh toán MoMo

    public PaymentResponse(String transactionId) {
        this.transactionId = transactionId;
    }

    public PaymentResponse(String transactionId, String payUrl) {
        this.transactionId = transactionId;
        this.payUrl = payUrl;
    }
}
