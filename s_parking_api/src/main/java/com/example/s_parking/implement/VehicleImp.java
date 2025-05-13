package com.example.s_parking.implement;

import com.example.s_parking.entity.Vehicle;
import com.example.s_parking.repository.VehicleRepository;
import com.example.s_parking.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleImp implements VehicleService {

    @Autowired
    VehicleRepository vehicleRepository;

    @Override
    public List<Vehicle> getVehiclesByUserUsername(String username) {
        return vehicleRepository.getVehiclesByUserUsername(username);
    }
}
