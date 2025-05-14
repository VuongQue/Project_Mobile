package com.example.s_parking.repository;

import com.example.s_parking.entity.Booking;
import com.example.s_parking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByUserAndDate(User user, LocalDate date);

    List<Booking> findByUser(User user);

    Booking findTopByUserOrderByCreatedAtDesc(User user);

    List<Booking> findByUserAndPaymentIsNull(User user);
}
