package com.example.s_parking.repository;

import com.example.s_parking.entity.Booking;
import com.example.s_parking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByUserAndDate(User user, LocalDate date);

    List<Booking> findByUser(User user);

    Booking findTopByUserUsernameOrderByCreatedAtDesc(String username);
    Booking findTopByUserUsernameAndDateOrderByCreatedAtDesc(String username, LocalDate date);

    List<Booking> getBookingsByUserUsername(String username);

    List<Booking> findByUserAndPaymentIsNull(User user);
    @Query("SELECT b FROM Booking b WHERE b.payment IS NULL AND b.createdAt < :expirationTime")
    List<Booking> findByPaymentIsNullAndCreatedAtBefore(@Param("expirationTime") LocalDateTime expirationTime);
}
