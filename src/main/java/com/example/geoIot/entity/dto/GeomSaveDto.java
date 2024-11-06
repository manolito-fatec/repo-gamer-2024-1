package com.example.geoIot.entity.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeomSaveDto {
    private String name;
    private String shape;
    private List<CoordinateDto> coordinates;
    private CoordinateDto center;
    private Double radius;
}
