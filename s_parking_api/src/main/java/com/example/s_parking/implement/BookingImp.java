package com.example.s_parking.service.impl;

import com.example.s_parking.dto.request.BookingRequest;
import com.example.s_parking.dto.response.BookingResponse;
import com.example.s_parking.entity.Booking;
import com.example.s_parking.entity.ParkingLot;
import com.example.s_parking.repository.BookingRepository;
import com.example.s_parking.repository.ParkingLotRepository;
import com.example.s_parking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BookingImp implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    private static final double FIXED_FEE = 7000;

    @Override
    public BookingResponse createBooking(BookingRequest request) {
        Long idParking = request.getIdParking();
        Optional<ParkingLot> parkingLot = parkingLotRepository.findById(idParking);

        if (parkingLot.isPresent() && "AVAILABLE".equals(parkingLot.get().getStatus())) {
            Booking booking = new Booking();
            booking.setCreatedAt(LocalDateTime.now());
            booking.setDate(LocalDateTime.now().toLocalDate());
            booking.setFee(FIXED_FEE);
            booking.setIdParking(idParking);
            booking.setUsername(request.getUsername());
            booking.setIdPayment(null); // Xử lý thanh toán sẽ bổ sung sau

            Booking savedBooking = bookingRepository.save(booking);

            parkingLot.get().setStatus("RESERVED");
            parkingLotRepository.save(parkingLot.get());

            return new BookingResponse(savedBooking.getId(), savedBooking.getCreatedAt(), savedBooking.getFee(), savedBooking.getUsername());
        }

        return null;
    }
}
