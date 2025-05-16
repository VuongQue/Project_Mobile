package com.example.s_parking.service;

import com.example.s_parking.dto.response.MyCurrentSessionResponse;
import com.example.s_parking.dto.response.SessionResponse;
import com.example.s_parking.entity.Payment;
import com.example.s_parking.entity.Session;

import java.util.List;
import java.util.Optional;

public interface SessionService {
    List<Session> getAllSessions();
    Optional<Session> getSessionById(Long id);
    Session createSession(Session session);
    Session updateSession(Session session);
    void deleteSession(Long id);
    void updateSessionIdPayment(List<Session> sessions, Payment payment);

    List<Session> getSessionByUsername(String username);

    List<SessionResponse> convertAllToDto(List<Session> list);

    SessionResponse convertToDto(Session entity);

    MyCurrentSessionResponse convertToMyDto(Session entity);

    Session getMyCurrentSession(String username);

    List<Session> getUnpaidSessions(String username);
    List<Session> getSessionsByIds(List<Long> ids);
    void saveAllSessions(List<Session> sessions);
}
