package com.example.s_parking.service;

import com.example.s_parking.dto.request.BookingRequest;
import com.example.s_parking.dto.response.BookingResponse;
import com.example.s_parking.entity.Booking;
import com.example.s_parking.entity.Payment;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingService {
    List<Booking> getAllBookings();
    Optional<Booking> getBookingById(Long id);

    BookingResponse createBooking(BookingRequest bookingRequest);


    Booking createBooking(Booking booking);

    Booking updateBooking(Long id, Booking booking);
    void deleteBooking(Long id);

    Optional<Booking> findByUsernameAndDate(String username, LocalDate date);

    List<Booking> getBookingByUserUsername(String username);

    List<BookingResponse> convertAllToDto(List<Booking> bookingList);

    List<BookingResponse> convertToDtoList(List<Booking> bookingList);

    BookingResponse convertToDto(Booking entity);

    Booking getMyCurrentBooking(String username);

    List<Booking> getUnpaidBookings(String username);

    List<Booking> getBookingsByIds(List<Long> ids);

    void saveAllBookings(List<Booking> bookings);
    void updateBookingPayment(Long bookingId, Payment payment);
    void checkExpiredBookings();


}
