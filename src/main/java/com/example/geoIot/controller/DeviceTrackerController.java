package com.example.geoIot.controller;

import com.example.geoIot.entity.dto.DeviceTrackerDto;
import com.example.geoIot.entity.dto.DeviceTrackerPeriodRequestDto;
import com.example.geoIot.entity.dto.history.HistoryDto;
import com.example.geoIot.service.device.DeviceTrackerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Tag(name = "Consulta - Controller", description = "Endpoints para consultar dispositivos e pessoas por período")
@RestController
@RequestMapping("/tracker")
@CrossOrigin
public class DeviceTrackerController {

    @Autowired
    private DeviceTrackerService service;

    @GetMapping("/period/{personId}/{init}/{end}")
    @Operation(summary = "Consultar dados para plotagem", description = "Faz uma requisição ao OracleCloud trazendo os dados de um dispositivo por período")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pessoa encontrada com sucesso."),
            @ApiResponse(responseCode = "204", description = "Página da requisição vazia, os elementos acabaram na página anterior."),
            @ApiResponse(responseCode = "404", description = "Pessoa com o ID fornecido não foi encontrada."),
            @ApiResponse(responseCode = "408", description = "Tempo de resposta excedido."),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor ao tentar buscar a pessoa.")
    })
    public ResponseEntity<?> getByPeriod(
            @PathVariable Long personId,
            @PathVariable LocalDateTime init,
            @PathVariable LocalDateTime end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size
    ) {
        try {
            DeviceTrackerPeriodRequestDto requestDto = DeviceTrackerPeriodRequestDto.builder()
                    .personId(personId)
                    .init(init)
                    .end(end)
                    .build();
            Pageable pageable = PageRequest.of(page, size);
            Page<DeviceTrackerDto> dtoPage = service.getDeviceTrackerByDateInterval(requestDto, pageable);
            return ResponseEntity.ok(dtoPage);
        } catch (NoSuchElementException noSuchElementException) {
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/history")
    @Operation(summary = "Buscar o Histórico", description = "Realizar uma Requisição ao Oracle Cloud para Obter o Histórico de uma Pessoa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historico da Pessoa encontrado com sucesso."),
            @ApiResponse(responseCode = "204", description = "Página da requisição vazia, os elementos acabaram na página anterior."),
            @ApiResponse(responseCode = "404", description = "Pessoa com o ID fornecido não foi encontrada."),
            @ApiResponse(responseCode = "408", description = "Tempo de resposta excedido."),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor ao tentar buscar a pessoa.")
    })
    public ResponseEntity<?> getHistory(
            @RequestBody DeviceTrackerPeriodRequestDto filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        try {
            Pageable pageable = PageRequest.of(page,size);
            Page<HistoryDto> dtoPage = service.searchHistoryByDateInterval(filter, pageable);
            return ResponseEntity.ok(dtoPage);
        } catch (NoSuchElementException noSuchElementException) {
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    @GetMapping("/inside/{locationId}/{init}/{end}")
    @Operation(summary = "Buscar pontos dentro da área de uma forma", description = "Faz uma requisição ao OracleCloud trazendo uma lista de coordenadas dentro da forma requisitada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pontos encontrados com sucesso."),
            @ApiResponse(responseCode = "400", description = "Requisição má formulada"),
            @ApiResponse(responseCode = "404", description = "Local com o ID fornecido não foi encontrado."),
            @ApiResponse(responseCode = "408", description = "Tempo de resposta excedido."),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor ao tentar buscar a pessoa.")
    })
    public ResponseEntity<?> getTrackersInsideLocation(
        @Parameter(required = true) @PathVariable Long locationId,
        @Parameter(required = true) @PathVariable LocalDateTime init,
        @Parameter(required = true) @PathVariable LocalDateTime end,
        @Parameter() @RequestParam(required = false) Long userId
    ) {
        try {
            List<DeviceTrackerDto> deviceTrackers = service.getTrackersInsideLocation(locationId, init, end, userId);
            return ResponseEntity.ok(deviceTrackers);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonList("Error: " + e.getMessage()));
        }
    }

}
