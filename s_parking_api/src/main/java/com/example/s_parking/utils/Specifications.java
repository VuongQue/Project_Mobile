package com.example.s_parking.utils;

import com.example.s_parking.dto.response.ParkingLotResponse;
import com.example.s_parking.entity.ParkingLot;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class Specifications {
    public static Specification<ParkingLot> ParkingLotFilterBy(ParkingLotResponse request) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (request.getArea() != null && !request.getArea().isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("area"), request.getArea()));
            }
            if (request.getRow() != null && !request.getRow().isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("row"), request.getRow()));
            }
            if (request.getPos() != null && !request.getPos().isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("pos"), request.getPos()));
            }

            return predicate;
        };
    }
}
