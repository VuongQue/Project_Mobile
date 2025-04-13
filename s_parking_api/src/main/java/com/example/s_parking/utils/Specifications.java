package com.example.s_parking.utils;

import com.example.s_parking.dto.request.ParkingLotRequest;
import com.example.s_parking.entity.ParkingLot;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class Specifications {
    public static Specification<ParkingLot> ParkingLotFilterBy(ParkingLotRequest request) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (request.area != null && !request.area.isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("area"), request.area));
            }
            if (request.row != null && !request.row.isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("row"), request.row));
            }
            if (request.pos != null && !request.pos.isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("pos"), request.pos));
            }

            return predicate;
        };
    }
}
