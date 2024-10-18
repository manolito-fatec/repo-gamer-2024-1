package com.example.geoIot.entity.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class CoordinateDto {
    private double longitude;
    private double latitude;
}
