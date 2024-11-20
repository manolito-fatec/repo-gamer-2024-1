package com.example.geoIot.service.authentication;

import com.example.geoIot.entity.dto.auth.JwtAuthenticationResponseDto;
import com.example.geoIot.entity.dto.auth.LoginRequestDto;
import com.example.geoIot.entity.dto.auth.SignupRequestDto;

public interface AuthenticationService {

    JwtAuthenticationResponseDto signup(SignupRequestDto request);

    JwtAuthenticationResponseDto login(LoginRequestDto request);
}
