package com.example.geoIot.service.Location;

import com.example.geoIot.entity.Location;
import org.springframework.transaction.annotation.Transactional;

public interface LocationService {
    Location getLocation(Long id);
    Location saveLocation(String name, double[][] polyCoords);
}
