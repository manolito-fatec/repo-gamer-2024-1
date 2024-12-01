package com.example.geoIot.service.authentication;

import com.example.geoIot.entity.User;
import com.example.geoIot.entity.dto.UserDto;
import com.example.geoIot.entity.dto.auth.JwtAuthenticationResponseDto;
import com.example.geoIot.entity.dto.auth.LoginRequestDto;
import com.example.geoIot.entity.dto.auth.SignupRequestDto;
import com.example.geoIot.repository.UserRepository;
import com.example.geoIot.service.jwt.JwtService;
import com.example.geoIot.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationResponseDto signup(SignupRequestDto request) {
        var user = User
                .builder()
                .userEmail(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        userRepository.save(user);
        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponseDto.builder().token(jwt).build();
    }

    public JwtAuthenticationResponseDto login(LoginRequestDto request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));

            var user = userRepository.findByUserEmail(request.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));


            var jwt = jwtService.generateToken(user);
            return JwtAuthenticationResponseDto.builder().token(jwt).build();
        } catch (AuthenticationException e) {
            throw new IllegalArgumentException("Invalid credentials", e);
        }
    }

    @Override
    public Boolean getUSerByEmail(String email) {
        return email != null
                && !email.isBlank()
                && userRepository.findByUserEmail(email).isPresent();
    }

}
