package com.example.s_parking.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private String amount;   // Số tiền
    private String method;   // Phương thức thanh toán (BANK_TRANSFER, MOMO, ZALO_PAY, ...)
    private String orderInfo; // Thông tin đơn hàng (ví dụ: "Thanh toán đặt chỗ xe")
}
