package com.example.geoIot.controller;

import com.example.geoIot.entity.dto.DeviceTrackerDto;
import com.example.geoIot.entity.dto.DeviceTrackerPeriodRequestDto;
import com.example.geoIot.entity.dto.history.HistoryDto;
import com.example.geoIot.entity.dto.history.StopDto;
import com.example.geoIot.service.device.DeviceTrackerService;
import io.swagger.v3.oas.annotations.Operation;
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

    @GetMapping("/stop/{personId}/{init}/{end}")
    @Operation(summary = "Buscar pontos de parada", description = "Faz uma requisição ao OracleCloud trazendo os dados uma lista de paradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pessoa encontrada com sucesso."),
            @ApiResponse(responseCode = "204", description = "Página da requisição vazia, os elementos acabaram na página anterior."),
            @ApiResponse(responseCode = "404", description = "Pessoa com o ID fornecido não foi encontrada."),
            @ApiResponse(responseCode = "408", description = "Tempo de resposta excedido."),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor ao tentar buscar a pessoa.")
    })
    public ResponseEntity<?> getStop(
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
            Page<StopDto> stopList = service.getStopList(requestDto, pageable);
            return ResponseEntity.ok(stopList);
        } catch (NoSuchElementException noSuchElementException) {
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
