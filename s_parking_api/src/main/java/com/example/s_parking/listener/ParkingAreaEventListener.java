package com.example.s_parking.listener;

import com.example.s_parking.event.ParkingAreaUpdatedEvent;
import com.example.s_parking.service.ParkingSocketService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Component
public class ParkingAreaEventListener {

    private final ParkingSocketService parkingSocketService;

    public ParkingAreaEventListener(ParkingSocketService parkingSocketService) {
        this.parkingSocketService = parkingSocketService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onParkingAreaUpdated(ParkingAreaUpdatedEvent event) {
        // Gửi dữ liệu WebSocket sau khi DB commit
        System.out.println("Event received, sending socket update");
        parkingSocketService.updateSlots();
    }
}
