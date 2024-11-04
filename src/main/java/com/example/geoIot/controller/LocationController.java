package com.example.geoIot.controller;

import com.example.geoIot.entity.dto.LocationDto;
import com.example.geoIot.entity.dto.GeomSaveDto;
import com.example.geoIot.exception.OpenPolygonException;
import com.example.geoIot.service.location.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@Tag(name = "Local - Controller", description = "Endpoints para criar e consultar poligonos de localizações")
@RestController
@RequestMapping("/location")
@CrossOrigin
public class LocationController {

    @Autowired
    private LocationService service;

    @GetMapping("/get-shape")
    @Operation(summary = "Busca de uma forma de local", description = "Faz uma requisição ao OracleCloud trazendo os dados de uma forma")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Forma encontrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Requisição mal formulada."),
            @ApiResponse(responseCode = "404", description = "Forma não existe."),
            @ApiResponse(responseCode = "408", description = "Tempo de resposta excedido."),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor ao tentar buscar o local.")
    })
    public ResponseEntity<?> getShape(
            @Parameter(description = "ID do local", required = true) @RequestParam long id
    ) {
        try {
            LocationDto locationDto = service.getLocation(id);
            return ResponseEntity.ok().body(locationDto);
        } catch (NoSuchElementException noSuchElementException) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException illegalArgumentException) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException runtimeException) {
            return ResponseEntity.internalServerError().body("Internal Server Error" + runtimeException.getMessage());
        }
    }

    @GetMapping("/get-all-shapes")
    @Operation(summary = "Busca de todas as formas de locais", description = "Faz uma requisição ao OracleCloud trazendo todos os dados de formas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Todas as formas encontrados com sucesso."),
            @ApiResponse(responseCode = "400", description = "Requisição mal formulada."),
            @ApiResponse(responseCode = "404", description = "Nenhuma forma existe."),
            @ApiResponse(responseCode = "408", description = "Tempo de resposta excedido."),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor ao tentar buscar os locais.")
    })
    public ResponseEntity<?> getAllShapes() {
        try {
            List<LocationDto> locationDtoList = service.getAllLocations();
            return ResponseEntity.ok().body(locationDtoList);
        } catch (NoSuchElementException noSuchElementException) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException illegalArgumentException) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException runtimeException) {
            return ResponseEntity.internalServerError().body("Internal Server Error" + runtimeException.getMessage());
        }
    }

    @Operation(summary = "Inserção de um polígono de local", description = "Faz uma requisição ao OracleCloud salvando os dados de um polígono ou circulo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Polígono/Circulo criado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Requisição mal formulada."),
            @ApiResponse(responseCode = "408", description = "Tempo de resposta excedido."),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor ao tentar criar o local.")
    })
    @PostMapping("/save-shape")
    public ResponseEntity<?> saveShape(
            @Parameter(description = "Dados da geometria a ser salva",required = true) @RequestBody GeomSaveDto saveDto
    ) {
        try {
            LocationDto createdLocation = service.saveLocation(saveDto);
            return ResponseEntity.status(201).body(createdLocation);
        } catch (OpenPolygonException e) {
            return ResponseEntity.badRequest().body("Bad Request: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }
}
