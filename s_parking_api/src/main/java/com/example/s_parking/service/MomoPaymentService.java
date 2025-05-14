package com.example.s_parking.service;

public interface MomoPaymentService {
    String createPayment(String orderId, String amount, String orderInfo) throws Exception;
}
