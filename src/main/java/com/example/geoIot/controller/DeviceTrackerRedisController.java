package com.example.geoIot.controller;

import com.example.geoIot.entity.DeviceTrackerRedis;
import com.example.geoIot.entity.dto.DeviceTrackerRedisDto;
import com.example.geoIot.service.device.DeviceTrackerRedisService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Redis - Controller", description = "Endpoints para gerenciamento do Redis")
@RestController
@RequestMapping("/v2/device")
public class DeviceTrackerRedisController {

    @Autowired
    private DeviceTrackerRedisService deviceTrackerRedisService;

    @PostMapping
    @Operation(summary = "Sincronizar dados entre Redis e Oracle Cloud", description = "Salva os dados no armazenamento em memória do Redis e sincroniza-os com o Oracle Cloud. Esse processo garante que os dados armazenados em cache no Redis sejam replicados e atualizados no Oracle Database, mantendo a consistência entre o armazenamento em memória e o banco de dados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sincronização feita com sucesso"),
            @ApiResponse(responseCode = "400", description = "Sincronização não foi concluída"),
            @ApiResponse(responseCode = "408", description = "Tempo de resposta excedido."),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor ao tentar sincronizar.")
    })
    public ResponseEntity<String> save(@RequestBody List<DeviceTrackerRedisDto> deviceTrackerRedis) {
        deviceTrackerRedisService.saveDataInCache(deviceTrackerRedis);
        return ResponseEntity.status(201).body("OK");
    }
}
