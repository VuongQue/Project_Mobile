package com.example.s_parking.repository;

import com.example.s_parking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByUserUsernameAndDate(String username, LocalDate date);
}
