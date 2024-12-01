package com.example.geoIot.service.jwt;

import com.example.geoIot.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String extractUsername(String token);

    String extractUserEmail(String token);

    String generateToken(User user);

    boolean isTokenValid(String token, UserDetails userDetails);

}
