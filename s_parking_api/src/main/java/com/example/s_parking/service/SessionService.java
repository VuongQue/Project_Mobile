package com.example.s_parking.service;

import com.example.s_parking.dto.response.SessionResponse;
import com.example.s_parking.entity.Session;

import java.util.List;
import java.util.Optional;

public interface SessionService {
    List<Session> getAllSessions();
    Optional<Session> getSessionById(Long id);
    Session createSession(Session session);
    Session updateSession(Long id, Session session);
    void deleteSession(Long id);

    List<Session> getSessionByUsername(String username);

    List<SessionResponse> convertAllToDto(List<Session> list);

    SessionResponse convertToDto(Session entity);
}
