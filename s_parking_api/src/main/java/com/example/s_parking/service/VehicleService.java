package com.example.s_parking.service;

import com.example.s_parking.entity.Vehicle;

import java.util.List;

public interface VehicleService {
    List<Vehicle> getVehiclesByUserUsername(String username);
}
