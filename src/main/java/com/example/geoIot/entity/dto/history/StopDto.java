package com.example.geoIot.entity.dto.history;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class StopDto {
    private Double latitude;
    private Double longitude;
    private LocalDateTime timestamp;
    private LocationDto  stopLocation;
}
