package com.example.geoIot.entity.dto.history;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class HistoryDto {
    private LocalDateTime endTime;
    private LocalDateTime finalTime;
    private Double distanceBetweenPoints;
    private LocationDto initialLocation;
    private LocationDto finalLocation;
}
