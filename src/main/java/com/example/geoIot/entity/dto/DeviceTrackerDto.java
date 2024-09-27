package com.example.geoIot.entity.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DeviceTrackerDto {
    Long id;
    String itoId;
    LocalDateTime createdAt;
    BigDecimal latitude;
    BigDecimal longitude;
}
