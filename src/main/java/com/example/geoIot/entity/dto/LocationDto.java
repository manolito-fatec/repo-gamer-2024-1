package com.example.geoIot.entity.dto;

import lombok.*;
import org.locationtech.jts.geom.Polygon;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationDto {
    private Long idLocation;
    private String name;
    private String shape;
    private List<CoordinateDto> coordinates;
    private CoordinateDto center;
    private Double radius;
}
