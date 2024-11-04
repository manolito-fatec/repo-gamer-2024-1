package com.example.geoIot.service.location;

import com.example.geoIot.entity.dto.LocationDto;
import com.example.geoIot.entity.dto.GeomSaveDto;

import java.util.List;

public interface LocationService {
    LocationDto getLocation(Long id);
    List<LocationDto> getAllLocations();
    LocationDto saveLocation(GeomSaveDto geomSaveDto);
}
