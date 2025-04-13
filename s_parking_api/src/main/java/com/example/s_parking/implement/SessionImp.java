package com.example.s_parking.implement;

import com.example.s_parking.dto.response.SessionBasicInfo;
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
    public List<SessionBasicInfo> getSessionsByUsername(String username) {
        List<Session> sessions = sessionRepository.findByUser_Username(username);
        return sessions.stream().map(session -> new SessionBasicInfo(
                session.getId(),
                session.getUser().getUsername(),
                session.getParking().getLocation(),
                session.getCheckIn(),
                session.getCheckOut(),
                session.getLicensePlate(),
                session.getFee(),
                session.getPayment().getId()
        )).collect(Collectors.toList());
    }
}
