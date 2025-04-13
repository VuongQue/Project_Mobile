package com.example.s_parking.repository;

import com.example.s_parking.entity.ParkingArea;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingAreaRepository extends JpaRepository<ParkingArea, Long> {
}
