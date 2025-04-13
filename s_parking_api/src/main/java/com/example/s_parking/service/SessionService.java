package com.example.s_parking.service;

import com.example.s_parking.dto.response.SessionBasicInfo;
import com.example.s_parking.entity.Session;

import java.util.List;
import java.util.Optional;

public interface SessionService {
    List<Session> getAllSessions();
    Optional<Session> getSessionById(Long id);
    Session createSession(Session session);
    Session updateSession(Long id, Session session);
    void deleteSession(Long id);
    public List<SessionBasicInfo> getSessionsByUsername(String username);
}
