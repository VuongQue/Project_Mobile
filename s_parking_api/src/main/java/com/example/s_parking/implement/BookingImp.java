package com.example.s_parking.implement;

import com.example.s_parking.dto.request.BookingRequest;
import com.example.s_parking.dto.response.BookingResponse;
import com.example.s_parking.entity.Booking;
import com.example.s_parking.entity.ParkingLot;
import com.example.s_parking.entity.User;
import com.example.s_parking.repository.BookingRepository;
import com.example.s_parking.repository.ParkingLotRepository;
import com.example.s_parking.repository.UserRepository;
import com.example.s_parking.service.BookingService;
import com.example.s_parking.value.ParkingStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private static final float FIXED_FEE = 7000f;

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
        if (userOpt.isEmpty()) {
            System.out.println("User không tồn tại: " + request.getUsername());
            return null;
        }

        Optional<ParkingLot> parkingOpt = parkingLotRepository.findById(request.getIdParking());
        if (parkingOpt.isEmpty()) {
            System.out.println("Chỗ đậu xe không tồn tại: " + request.getIdParking());
            return null;
        }

        ParkingLot parking = parkingOpt.get();

        if (!ParkingStatus.AVAILABLE.equals(parking.getStatus())) {
            System.out.println("Chỗ đậu xe không khả dụng: " + parking.getId());
            return null;
        }

        // Tạo booking mới
        Booking booking = new Booking();
        booking.setCreatedAt(LocalDateTime.now());
        booking.setDate(LocalDate.now());
        booking.setFee(FIXED_FEE);
        booking.setUser(userOpt.get());
        booking.setParking(parking);
        booking.setPayment(null);

        // Cập nhật trạng thái chỗ đậu xe
        parking.setStatus(ParkingStatus.RESERVED);
        parkingLotRepository.save(parking);

        Booking savedBooking = bookingRepository.save(booking);
        return convertToDto(savedBooking);
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
        bookingRepository.deleteById(id);
    }

    @Override
    public Optional<Booking> findByUsernameAndDate(String username, LocalDate date) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        return userOpt.flatMap(user -> bookingRepository.findByUserAndDate(user, date));
    }

    @Override
    public List<Booking> getBookingByUserUsername(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        return userOpt.map(bookingRepository::findByUser).orElse(List.of());
    }

    @Override
    public List<BookingResponse> convertAllToDto(List<Booking> bookingList) {
        return List.of();
    }

    @Override
    public Booking getMyCurrentBooking(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        return userOpt.map(bookingRepository::findTopByUserOrderByCreatedAtDesc).orElse(null);
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
    public List<BookingResponse> convertToDtoList(List<Booking> bookingList) {
        return bookingList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
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
    public void saveAllBookings(List<Booking> bookings) {
        bookingRepository.saveAll(bookings);
    }
}
