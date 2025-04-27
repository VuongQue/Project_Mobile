package com.example.s_parking.repository;

import com.example.s_parking.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByTransactionId(String transactionId);
}
