package com.example.geoIot.service.location;

import com.example.geoIot.entity.Location;

public interface LocationService {
    Location getLocation(Long id);
    Location saveLocation(String name, double[][] polyCoords);
}
