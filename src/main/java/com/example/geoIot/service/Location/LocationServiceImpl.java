package com.example.geoIot.service.Location;

import com.example.geoIot.entity.Location;
import com.example.geoIot.repository.LocationRepository;
import oracle.spatial.geometry.JGeometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class LocationServiceImpl implements LocationService {

    @Autowired
    private LocationRepository locationRepository;

    private final int DIMENSIONS = 2;
    private final int SRID = 8307;

    @Transactional
    @Override
    public Location getLocation(Long id) {
        Optional<Location> location = locationRepository.findById(id);
        if (location.isEmpty()) {
            throw new NoSuchElementException("No such location");
        }
        return location.get();
    }

    @Transactional
    @Override
    public Location saveLocation(String name, double[][] polyCoords) {
        Location location = new Location();
        location.setName(name);

        return locationRepository.save(location);

    }
}
