package com.example.geoIot.controller;

import com.example.geoIot.entity.dto.DeviceTrackerDto;
import com.example.geoIot.entity.dto.DeviceTrackerPeriodRequestDto;
import com.example.geoIot.service.device.DeviceTrackerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
            @ApiResponse(responseCode = "404", description = "Pessoa com o ID fornecido não foi encontrada."),
            @ApiResponse(responseCode = "408", description = "Tempo de resposta excedido."),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor ao tentar buscar a pessoa.")
    })
    public ResponseEntity<?> getByPeriod(
            @PathVariable Long personId,
            @PathVariable LocalDateTime init,
            @PathVariable LocalDateTime end
    ) {
        try {
            DeviceTrackerPeriodRequestDto requestDto = DeviceTrackerPeriodRequestDto.builder()
                    .personId(personId)
                    .init(init)
                    .end(end)
                    .build();
            List<DeviceTrackerDto> dtoList = service.getDeviceTrackerByDateInterval(requestDto);
            return ResponseEntity.ok(dtoList);
        } catch (NoSuchElementException noSuchElementException) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}