package com.example.s_parking.service;

import java.util.List;
import java.util.Optional;

import com.example.s_parking.entity.Payment;

public interface PaymentService {
    List<Payment> getAllPayments();
    Optional<Payment> getPaymentById(Long id);
    Payment createPayment(Payment payment);
    Payment updatePayment(Long id, Payment payment);
    void deletePayment(Long id);
}
