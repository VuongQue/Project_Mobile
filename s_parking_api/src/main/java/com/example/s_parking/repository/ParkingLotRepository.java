package com.example.s_parking.repository;

import com.example.s_parking.entity.ParkingLot;
import com.example.s_parking.value.ParkingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long>, JpaSpecificationExecutor<ParkingLot> {
    Optional<ParkingLot> findFirstByStatus(ParkingStatus status);


    List<ParkingLot> findByStatus(ParkingStatus available);
}
