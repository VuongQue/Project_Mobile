package com.example.s_parking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "OTP")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OTP {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String purpose;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean isUsed;

    @ManyToOne
    @JoinColumn(name = "username")
    private User user;
}

