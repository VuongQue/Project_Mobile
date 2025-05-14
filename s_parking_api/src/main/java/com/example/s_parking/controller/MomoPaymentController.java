package com.example.s_parking.controller;

import com.example.s_parking.dto.request.MomoPaymentRequest;
import com.example.s_parking.dto.request.PaymentRequest;
import com.example.s_parking.service.MomoPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class MomoPaymentController {

    @Autowired
    private MomoPaymentService momoPaymentService;

    @PostMapping("/create")
    public String createPayment(@RequestBody MomoPaymentRequest momoPaymentRequest) {
        try {
            return momoPaymentService.createPayment(
                    momoPaymentRequest.getOrderId(),
                    momoPaymentRequest.getAmount(),
                    momoPaymentRequest.getOrderInfo()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/notify")
    public String handleNotify(@RequestBody Map<String, Object> payload) {
        System.out.println("Received MoMo notification: " + payload);
        return "Notification received";
    }
}
