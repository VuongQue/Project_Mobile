package com.example.s_parking.service;

import com.example.s_parking.dto.request.ConfirmPaymentRequest;
import com.example.s_parking.dto.request.PaymentRequest;
import com.example.s_parking.dto.response.PaymentResponse;

public interface PaymentService {
    PaymentResponse createPayment(PaymentRequest request, String username);

    PaymentResponse createMomoPayment(PaymentRequest request, String username);

    String confirmPayment(ConfirmPaymentRequest request, String username);

    String updatePaymentStatus(String transactionId, String status);
    PaymentResponse createZaloPayPayment(PaymentRequest request, String username);

}