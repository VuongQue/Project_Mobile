package com.example.s_parking.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "parking_area")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingArea {
    @Id
    private String idArea;

    private int maxCapacity;
    private int availableSlots;

    @OneToMany(mappedBy = "area", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ParkingLot> parkingLots;
}
