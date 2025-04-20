package com.example.s_parking.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Entity
@Table(name = "parking_lot")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingLot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @JsonProperty("area")
    public String getIdArea() {
        return area != null ? area.getIdArea() : null;
    }

    @Column(name = "`row`")
    private String row;
    private String pos;
    private String status;

    

    @ManyToOne
    @JoinColumn(name = "area")
    @JsonBackReference
    private ParkingArea area;

    public String getLocation() {
        return area.getIdArea() + " - " + row + pos;
    }
}

