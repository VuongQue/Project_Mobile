package com.example.s_parking.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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
    private String fullname;
    private String email;
    private String security_key;
    private boolean isActivate;
}
