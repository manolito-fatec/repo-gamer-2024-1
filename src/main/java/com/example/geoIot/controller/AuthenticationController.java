package com.example.geoIot.controller;

import com.example.geoIot.entity.dto.auth.JwtAuthenticationResponseDto;
import com.example.geoIot.entity.dto.auth.LoginRequestDto;
import com.example.geoIot.entity.dto.auth.SignupRequestDto;
import com.example.geoIot.service.authentication.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Operation(summary = "Cadastro de Usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Requisição mal formulada."),
            @ApiResponse(responseCode = "408", description = "Tempo de resposta excedido."),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor ao tentar buscar o local.")
    })
    @PostMapping("/signup")
    public ResponseEntity<JwtAuthenticationResponseDto> signup(@RequestBody SignupRequestDto request) {
        JwtAuthenticationResponseDto response = authenticationService.signup(request);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "Login de Usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Requisição mal formulada."),
            @ApiResponse(responseCode = "408", description = "Tempo de resposta excedido."),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor ao tentar buscar o local.")
    })
    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponseDto> login(@RequestBody LoginRequestDto request) {
        JwtAuthenticationResponseDto response = authenticationService.login(request);
        return ResponseEntity.status(200).body(response);
    }


}
