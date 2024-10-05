package com.example.geoIot.controller;

import com.example.geoIot.entity.dto.LocationDto;
import com.example.geoIot.entity.dto.PolygonSaveDto;
import com.example.geoIot.service.location.LocationService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/location")
public class LocationController {

    @Autowired
    private LocationService service;

    @GetMapping("get-polygon")
    public ResponseEntity<?> getPolygon(@RequestParam long id) {
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

    @PostMapping("/save-polygon")
    public ResponseEntity<?> savePolygon(@RequestParam PolygonSaveDto saveDto) {
        try {
            service.saveLocation(saveDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Bad Request: " + e.getMessage());
        }

        return null;
    }
}
