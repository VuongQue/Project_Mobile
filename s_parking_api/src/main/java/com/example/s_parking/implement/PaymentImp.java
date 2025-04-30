package com.example.s_parking.implement;

import com.example.s_parking.dto.request.ConfirmPaymentRequest;
import com.example.s_parking.dto.request.PaymentRequest;
import com.example.s_parking.dto.response.PaymentResponse;
import com.example.s_parking.entity.Payment;
import com.example.s_parking.entity.Session;
import com.example.s_parking.repository.PaymentRepository;
import com.example.s_parking.repository.SessionRepository;
import com.example.s_parking.service.PaymentService;
import com.example.s_parking.value.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentImp implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final SessionRepository sessionRepository;

    @Override
    public PaymentResponse createPayment(PaymentRequest request, String username) {
        Payment payment = new Payment();
        payment.setAmount(request.getAmount());
        payment.setMethod(request.getMethod());
        payment.setStatus(PaymentStatus.UNPAID);
        payment.setTransactionId(generateTransactionId());
        payment.setCreatedAt(LocalDateTime.now());

        paymentRepository.save(payment);

        return new PaymentResponse(payment.getTransactionId()); // <-- trả về DTO gọn
    }


    @Override
    public String confirmPayment(ConfirmPaymentRequest request, String username) {
        Optional<Payment> optionalPayment = paymentRepository.findByTransactionId(request.getTransactionId());
        if (optionalPayment.isEmpty()) {
            throw new RuntimeException("Không tìm thấy giao dịch.");
        }

        Payment payment = optionalPayment.get();

        if (payment.getStatus() == PaymentStatus.PAID) {
            throw new RuntimeException("Giao dịch này đã được thanh toán trước đó.");
        }

        List<Session> sessions = sessionRepository.findAllById(request.getSessionIds());

        boolean isUserOwner = sessions.stream()
                .allMatch(session -> session.getUser().getUsername().equals(username));

        if (!isUserOwner) {
            throw new RuntimeException("Bạn không có quyền xác nhận giao dịch.");
        }

        for (Session session : sessions) {
            session.setPayment(payment);
        }
        sessionRepository.saveAll(sessions);

        payment.setStatus(PaymentStatus.PAID);
        paymentRepository.save(payment);

        return "Đã xác nhận thanh toán thành công.";
    }

    private String generateTransactionId() {
        return "TRANS_" + UUID.randomUUID().toString().replace("-", "").substring(0, 15).toUpperCase();
    }
}
