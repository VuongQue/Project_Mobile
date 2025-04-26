package com.example.s_parking.service;

import com.example.s_parking.dto.response.BookingResponse;
import com.example.s_parking.entity.Booking;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingService {
    List<Booking> getAllBookings();
    Optional<Booking> getBookingById(Long id);
    Booking createBooking(Booking booking);
    Booking updateBooking(Long id, Booking booking);
    void deleteBooking(Long id);

    Optional<Booking> findByUsernameAndDate(String username, LocalDate date);

    List<Booking> getBookingByUserUsername(String username);

    List<BookingResponse> convertAllToDto(List<Booking> bookingList);

    BookingResponse convertToDto(Booking entity);
}
