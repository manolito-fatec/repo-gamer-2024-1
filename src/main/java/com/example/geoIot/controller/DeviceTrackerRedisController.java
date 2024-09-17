package com.example.geoIot.controller;

import com.example.geoIot.entity.DeviceTrackerRedis;
import com.example.geoIot.entity.dto.DeviceTrackerRedisDto;
import com.example.geoIot.service.device.DeviceTrackerRedisService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v2/device")
public class DeviceTrackerRedisController {

    @Autowired
    private DeviceTrackerRedisService deviceTrackerRedisService;

    @PostMapping
    public ResponseEntity<String> save(@RequestBody List<DeviceTrackerRedisDto> deviceTrackerRedis) {
        deviceTrackerRedisService.saveDataInCache(deviceTrackerRedis);
        return ResponseEntity.status(201).body("OK");
    }
}
