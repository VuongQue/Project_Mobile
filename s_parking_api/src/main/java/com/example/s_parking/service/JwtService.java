package com.example.s_parking.service;

public interface JwtService {
    public String generateToken(String username, String role);
    public String extractUsername(String token);
    public String extractRole(String token);
    public boolean isTokenValid(String token);
}
