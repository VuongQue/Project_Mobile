package com.example.s_parking.implement;

import com.example.s_parking.dto.response.MyCurrentSessionResponse;
import com.example.s_parking.dto.response.SessionResponse;
import com.example.s_parking.entity.Payment;
import com.example.s_parking.entity.Session;
import com.example.s_parking.repository.SessionRepository;
import com.example.s_parking.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SessionImp implements SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    @Override
    public List<Session> getAllSessions() {
        return sessionRepository.findAll();
    }

    @Override
    public Optional<Session> getSessionById(Long id) {
        return sessionRepository.findById(id);
    }

    @Override
    public Session createSession(Session session) {
        return sessionRepository.save(session);
    }

    @Override
    public Session updateSession(Session session) {
        try {
            if (sessionRepository.existsById(session.getId())) {
                return sessionRepository.save(session);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteSession(Long id) {
        sessionRepository.deleteById(id);
    }

    @Override
    public void updateSessionIdPayment(List<Session> sessions, Payment payment) {
        if (sessions == null || sessions.isEmpty()) {
            return; // Không có session nào cần cập nhật
        }

        // Cập nhật trường idPayment cho từng session
        for (Session session : sessions) {
            session.setPayment(payment);
        }

        // Lưu danh sách session đã cập nhật
        sessionRepository.saveAll(sessions);
    }


    @Override
    public List<Session> getSessionByUsername(String username) {
        return sessionRepository.findByUserUsername(username);
    }

    @Override
    public List<SessionResponse> convertAllToDto(List<Session> list) {
        return list.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    @Override
    public SessionResponse convertToDto(Session entity) {
        return SessionResponse.builder()
                .id(entity.getId())
                .checkIn(entity.getCheckIn())
                .checkOut(entity.getCheckOut())
                .type(String.valueOf(entity.getType()))
                .licensePlate(entity.getLicensePlate())
                .fee(entity.getFee())
                .username(
                        entity.getUser() != null ? entity.getUser().getUsername() : null
                )
                .idParking(
                        entity.getParking() != null ? entity.getParking().getId() : null
                )
                .idPayment(
                        entity.getPayment() != null ? entity.getPayment().getId() : null
                )
                .build();
    }

    @Override
    public MyCurrentSessionResponse convertToMyDto(Session entity) {
        return MyCurrentSessionResponse.builder()
                .id(entity.getId())
                .checkIn(entity.getCheckIn())
                .checkOut(entity.getCheckOut())
                .type(String.valueOf(entity.getType()))
                .licensePlate(entity.getLicensePlate())
                .fee(entity.getFee())
                .location(entity.getParking().getLocation())
                .build();
    }

    @Override
    public Session getMyCurrentSession(String username) {
        return sessionRepository.findTopByUserUsernameOrderByCheckInDesc(username);
    }

    @Override
    public List<Session> getUnpaidSessions(String username) {
        return sessionRepository.findByUserUsernameAndPaymentIsNull(username);
    }

    @Override
    public List<Session> getSessionsByIds(List<Long> ids) {
        return List.of();
    }

    @Override
    public void saveAllSessions(List<Session> sessions) {

    }

}
