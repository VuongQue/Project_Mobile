package com.example.s_parking.implement;

import com.example.s_parking.dto.response.ParkingAreaResponse;
import com.example.s_parking.entity.ParkingArea;
import com.example.s_parking.event.ParkingAreaUpdatedEvent;
import com.example.s_parking.repository.ParkingAreaRepository;
import com.example.s_parking.service.ParkingAreaService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ParkingAreaImp implements ParkingAreaService {

    @Autowired
    private ParkingAreaRepository parkingAreaRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public List<ParkingArea> getParkingAreas() {
        return parkingAreaRepository.findAll();
    }

    @Override
    public ParkingAreaResponse convertToDto(ParkingArea entity) {
        return ParkingAreaResponse.builder()
                .idArea(entity.getIdArea())
                .availableSlots(entity.getAvailableSlots())
                .maxCapacity(entity.getMaxCapacity())
                .status(Objects.equals(entity.getAvailableSlots(), 0) ? "UNAVAILABLE" : "AVAILABLE")
                .build();
    }

    @Override
    public List<ParkingAreaResponse> convertAllToDto(List<ParkingArea> list) {
        return list.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public void updateSlots() {
        parkingAreaRepository.updateAvailableSlotsForAllAreas();
        entityManager.flush();
        entityManager.clear();;
        eventPublisher.publishEvent(new ParkingAreaUpdatedEvent());
    }

}
