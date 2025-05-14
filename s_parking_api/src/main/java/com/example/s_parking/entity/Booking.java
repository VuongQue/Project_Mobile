package com.example.s_parking.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt;
    private LocalDate date;
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

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public float getFee() {
        return fee;
    }

    public void setFee(float fee) {
        this.fee = fee;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ParkingLot getParking() {
        return parking;
    }

    public void setParking(ParkingLot parking) {
        this.parking = parking;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}
