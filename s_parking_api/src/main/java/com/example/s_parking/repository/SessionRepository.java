package com.example.s_parking.repository;

import com.example.s_parking.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByUserUsername(String username);

    Session findTopByUserUsernameOrderByCheckInDesc(String username);
}
