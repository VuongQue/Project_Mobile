package com.example.s_parking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_db")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private String username;

    private String password;
    private String role;

    @Column(name = "fullname")
    private String fullName;
    private String email;
    private String phone;
    private String avatarUrl;
    private boolean isActivate;
}
