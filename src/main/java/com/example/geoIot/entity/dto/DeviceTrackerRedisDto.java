package com.example.geoIot.entity.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DeviceTrackerRedisDto(
        String Id,
        LocalDateTime CreatedAt,
        BigDecimal Latitude,
        BigDecimal Longitude,
        String Fullname,
        String Code
) {
}
