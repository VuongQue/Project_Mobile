package com.example.s_parking.dto.request;

import lombok.Data;

@Data
public class PaymentRequest {
    private double amount;
    private String method;
    private String status;
    private String transactionId;
}
