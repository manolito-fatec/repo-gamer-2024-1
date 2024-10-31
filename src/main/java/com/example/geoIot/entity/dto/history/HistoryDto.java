package com.example.geoIot.entity.dto.history;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class HistoryDto {
    private LocalDateTime initDateTime;
    private LocalDateTime endDateTime;
    private Double distance;
    private LocationDto initial;
    private LocationDto finality;
}
