package com.example.geoIot.service.location;

import com.example.geoIot.entity.Location;
import com.example.geoIot.entity.dto.CoordinateDto;

import com.example.geoIot.entity.dto.LocationDto;
import com.example.geoIot.entity.dto.PolygonSaveDto;
import com.example.geoIot.repository.LocationRepository;
import com.example.geoIot.util.CoordinateValidator;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class LocationServiceImpl implements LocationService {

    @Autowired
    private LocationRepository locationRepository;

    private CoordinateValidator coordinateValidator;

    @Transactional
    @Override
    public LocationDto getLocation(Long id) {
        Optional<Location> location = locationRepository.findById(id);
        if (location.isEmpty()) {
            throw new NoSuchElementException("No such location");
        }
        return LocationDto.builder()
                .idLocation(location.get().getIdLocation())
                .name(location.get().getName())
                .polygon(location.get().getPolygon())
                .build();
    }

    @Transactional
    @Override
    public LocationDto saveLocation(PolygonSaveDto polygonSaveDto) {
        Location location = new Location();
        location.setName(polygonSaveDto.getName());

        List<Coordinate> coords = new ArrayList<>();
        for (CoordinateDto dotCoords : polygonSaveDto.getCoordinates()) {
            coordinateValidator.validateCoordinate(
                    dotCoords.getLongitude(),
                    dotCoords.getLatitude()
            );

            coords.add(new Coordinate(dotCoords.getLongitude(), dotCoords.getLatitude()));
        }
        GeometryFactory geometry = new GeometryFactory();

        location.setPolygon(geometry.createPolygon((coords.toArray(new Coordinate[0]))));

        Location savedLocation = locationRepository.save(location);
        return LocationDto.builder()
                .idLocation(savedLocation.getIdLocation())
                .name(savedLocation.getName())
                .polygon(savedLocation.getPolygon())
                .build();

    }
}
