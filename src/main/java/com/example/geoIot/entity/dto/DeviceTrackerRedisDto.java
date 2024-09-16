package com.example.geoIot.entity.dto;

public record DeviceTrackerRedisDto(
        String Id,
        String CreatedAt,
        Double Latitude,
        Double Longitude,
        String FullName,
        String Code
) {
}
