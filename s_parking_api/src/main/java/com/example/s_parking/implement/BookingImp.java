package com.example.s_parking.implement;

import com.example.s_parking.dto.request.BookingRequest;
import com.example.s_parking.dto.response.BookingResponse;
import com.example.s_parking.entity.Booking;
import com.example.s_parking.entity.ParkingLot;
import com.example.s_parking.entity.Payment;
import com.example.s_parking.entity.User;
import com.example.s_parking.repository.BookingRepository;
import com.example.s_parking.repository.ParkingLotRepository;
import com.example.s_parking.repository.PaymentRepository;
import com.example.s_parking.repository.UserRepository;
import com.example.s_parking.service.BookingService;
import com.example.s_parking.value.ParkingStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingImp implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private static final float FIXED_FEE = 7000f;
    private static final long BOOKING_EXPIRATION_MINUTES = 10;

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    @Override
    public BookingResponse createBooking(BookingRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        Optional<ParkingLot> parkingOpt = parkingLotRepository.findById(request.getIdParking());

        if (userOpt.isPresent() && parkingOpt.isPresent() && parkingOpt.get().getStatus() == ParkingStatus.AVAILABLE) {
            Booking booking = new Booking();
            booking.setCreatedAt(LocalDateTime.now());
            booking.setDate(LocalDate.now());
            booking.setFee(FIXED_FEE);
            booking.setUser(userOpt.get());
            booking.setParking(parkingOpt.get());
            booking.setPayment(null); // Chưa thanh toán

            ParkingLot parkingLot = parkingOpt.get();
            parkingLot.setStatus(ParkingStatus.RESERVED);
            parkingLotRepository.save(parkingLot);

            return convertToDto(bookingRepository.save(booking));
        }
        return null;
    }

    @Override
    public Booking createBooking(Booking booking) {
        return null;
    }

    @Override
    public Booking updateBooking(Long id, Booking booking) {
        if (bookingRepository.existsById(id)) {
            booking.setId(id);
            return bookingRepository.save(booking);
        }
        return null;
    }

    @Override
    public void deleteBooking(Long id) {
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            ParkingLot parkingLot = booking.getParking();
            if (parkingLot != null) {
                parkingLot.setStatus(ParkingStatus.AVAILABLE);
                parkingLotRepository.save(parkingLot);
            }
            bookingRepository.delete(booking);
        }
    }

    @Override
    public Optional<Booking> findByUsernameAndDate(String username, LocalDate date) {
        return Optional.empty();
    }

    @Override
    public List<Booking> getBookingByUserUsername(String username) {
        return bookingRepository.getBookingsByUserUsername(username);
    }

    @Override
    public List<BookingResponse> convertAllToDto(List<Booking> bookingList) {
        if (bookingList == null || bookingList.isEmpty()) {
            return Collections.emptyList();
        }

        return bookingList.stream()
                .map(booking -> {
                    BookingResponse dto = new BookingResponse();
                    dto.setId(booking.getId());
                    dto.setCreatedAt(booking.getCreatedAt());
                    dto.setDate(booking.getDate());
                    dto.setFee(booking.getFee());
                    dto.setIdParking(booking.getParking().getId());
                    dto.setUsername(booking.getUser().getUsername());
                    dto.setIdPayment(booking.getPayment().getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }


    @Override
    public void updateBookingPayment(Long bookingId, Long paymentId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);

        if (bookingOpt.isPresent() && paymentOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.setPayment(paymentOpt.get());

            ParkingLot parkingLot = booking.getParking();
            if (parkingLot != null) {
                parkingLot.setStatus(ParkingStatus.RESERVED);
                parkingLotRepository.save(parkingLot);
            }

            bookingRepository.save(booking);
        }
    }

    /**
     * Kiểm tra và xoá các booking quá hạn 10 phút
     */
    @Scheduled(cron = "0 * * * * *")
    public void checkExpiredBookings() {
        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(BOOKING_EXPIRATION_MINUTES);
        List<Booking> expiredBookings = bookingRepository.findByPaymentIsNullAndCreatedAtBefore(expirationTime);

        for (Booking booking : expiredBookings) {
            ParkingLot parkingLot = booking.getParking();
            if (parkingLot != null) {
                parkingLot.setStatus(ParkingStatus.AVAILABLE);
                parkingLotRepository.save(parkingLot);
            }

            bookingRepository.delete(booking);
        }
    }

    @Override
    public List<BookingResponse> convertToDtoList(List<Booking> bookingList) {
        return bookingList.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public BookingResponse convertToDto(Booking entity) {
        return BookingResponse.builder()
                .id(entity.getId())
                .createdAt(entity.getCreatedAt())
                .date(entity.getDate())
                .fee(entity.getFee())
                .idParking(entity.getParking().getId())
                .username(entity.getUser().getUsername())
                .idPayment(entity.getPayment() != null ? entity.getPayment().getId() : null)
                .build();
    }

    @Override
    public Booking getMyCurrentBooking(String username) {
        return null;
    }

    @Override
    public List<Booking> getUnpaidBookings(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        return userOpt.map(bookingRepository::findByUserAndPaymentIsNull).orElse(List.of());
    }

    @Override
    public List<Booking> getBookingsByIds(List<Long> ids) {
        return List.of();
    }

    @Override
    public void saveAllBookings(List<Booking> bookings) {

    }
}
