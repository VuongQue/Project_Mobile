package com.example.s_parking.entity;

import com.example.s_parking.value.SessionType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Session")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private String licensePlate;

    @Enumerated(EnumType.STRING)
    private SessionType type;
    private float fee;

    @ManyToOne
    @JoinColumn(name = "username")
    private User user;

    @ManyToOne
    @JoinColumn(name = "idParking")
    private ParkingLot parking;

    @ManyToOne
    @JoinColumn(name = "idPayment")
    private Payment payment;

}

