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
    private double amount;   // số tiền
    private String method;   // phương thức thanh toán (BANK_TRANSFER, MOMO, ZALO_PAY,...)
}
