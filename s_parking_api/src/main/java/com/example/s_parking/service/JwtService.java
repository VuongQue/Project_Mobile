package com.example.s_parking.service;

public interface JwtService {
    public String generateToken(String username);
    public String extractUsername(String token);
    public boolean isTokenValid(String token);
}
