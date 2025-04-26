package com.example.s_parking.implement;

import com.example.s_parking.dto.response.BookingResponse;
import com.example.s_parking.entity.Booking;
import com.example.s_parking.repository.BookingRepository;
import com.example.s_parking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingImp implements BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    @Override
    public Booking createBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    @Override
    public Booking updateBooking(Long id, Booking booking) {
        if (bookingRepository.existsById(id)) {
            booking.setId(id);
            return bookingRepository.save(booking);
        }
        throw new RuntimeException("Booking not found");
    }

    @Override
    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    @Override
    public Optional<Booking> findByUsernameAndDate(String username, LocalDate date) {
        return bookingRepository.findByUserUsernameAndDate(username, date);
    }

    @Override
    public List<Booking> getBookingByUserUsername(String username) {
        return bookingRepository.findByUserUsername(username);
    }

    @Override
    public List<BookingResponse> convertAllToDto(List<Booking> list) {
        return list.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    @Override
    public BookingResponse convertToDto(Booking entity) {
        return BookingResponse.builder()
                .id(entity.getId())
                .location(entity.getParking().getLocation())
                .date(entity.getDate())
                .createdAt(entity.getCreatedAt())
                .fee(entity.getFee())
                .build();
    }
}
