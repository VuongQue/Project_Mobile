package com.example.s_parking.repository;

import com.example.s_parking.entity.ParkingArea;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ParkingAreaRepository extends JpaRepository<ParkingArea, Long> {
    @Transactional
    @Modifying
    @Query("UPDATE ParkingArea a SET a.availableSlots = (" +
            "SELECT COUNT(p) FROM ParkingLot p WHERE p.area = a AND p.status = 'AVAILABLE')")
    void updateAvailableSlotsForAllAreas();
}
