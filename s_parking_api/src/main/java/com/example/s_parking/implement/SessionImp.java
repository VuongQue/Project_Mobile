package com.example.s_parking.implement;

import com.example.s_parking.dto.response.SessionResponse;
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
    public Session updateSession(Long id, Session session) {
        if (sessionRepository.existsById(id)) {
            session.setId(id);
            return sessionRepository.save(session);
        }
        throw new RuntimeException("Session not found");
    }

    @Override
    public void deleteSession(Long id) {
        sessionRepository.deleteById(id);
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
                .type(entity.getType())
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

}
