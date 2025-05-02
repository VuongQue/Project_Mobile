package com.example.s_parking.repository;

import com.example.s_parking.entity.OTP;
import com.example.s_parking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OTPRepository extends JpaRepository<OTP, Long> {
    Optional<OTP> findTopByUserAndPurposeAndIsUsedFalseOrderByCreatedAtDesc(User user, String purpose);
}
