package com.example.geoIot.controller;

import com.example.geoIot.entity.dto.DeviceTrackerDto;
import com.example.geoIot.entity.dto.DeviceTrackerPeriodRequestDto;
import com.example.geoIot.service.device.DeviceTrackerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/tracker")
public class DeviceTrackerController {

    @Autowired
    private DeviceTrackerService service;

    @GetMapping("/period/")
    public ResponseEntity<?> getByPeriod(
            @RequestParam Long personId,
            @RequestParam LocalDateTime init,
            @RequestParam LocalDateTime end
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
