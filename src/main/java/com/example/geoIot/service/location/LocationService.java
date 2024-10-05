package com.example.geoIot.service.location;

import com.example.geoIot.entity.dto.LocationDto;
import com.example.geoIot.entity.dto.PolygonSaveDto;

public interface LocationService {
    LocationDto getLocation(Long id);
    LocationDto saveLocation(PolygonSaveDto polygonSaveDto);
}
