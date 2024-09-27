package com.example.geoIot.entity.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DeviceTrackerPeriodRequestDto {
    Long personId;
    LocalDateTime init;
    LocalDateTime end;
}
