package com.example.geoIot.service.location;

import com.example.geoIot.entity.Location;
import com.example.geoIot.entity.dto.CoordinateDto;

import com.example.geoIot.entity.dto.LocationDto;
import com.example.geoIot.entity.dto.GeomSaveDto;
import com.example.geoIot.exception.OpenPolygonException;
import com.example.geoIot.repository.LocationRepository;
import com.example.geoIot.util.CoordinateValidator;
import org.locationtech.jts.geom.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class LocationServiceImpl implements LocationService {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private CoordinateValidator coordinateValidator;

    @Transactional
    @Override
    public LocationDto getLocation(Long id) {
        Optional<Location> locationOpt = locationRepository.findById(id);
        if (locationOpt.isEmpty()) {
            throw new NoSuchElementException("No such location");
        }

        return buildLocationDto(locationOpt.get());
    }

    @Override
    public List<LocationDto> getAllLocations() {
        List<Location> locationList = locationRepository.findAll();
        if (locationList.isEmpty()) {
            throw new NoSuchElementException("No locations exist yet");
        }
        return locationList.stream()
                .map(this::buildLocationDto) // Use the new buildLocationDto method
                .toList();
    }

    @Transactional
    @Override
    public LocationDto saveLocation(GeomSaveDto geomSaveDto) {
        if ("CIRCLE".equals(geomSaveDto.getShape()) && (geomSaveDto.getRadius() == null || geomSaveDto.getRadius() <= 0)) {
            throw new IllegalArgumentException("Radius is required for circle shapes and must be positive");
        }
        Location location = new Location();
        location.setName(geomSaveDto.getName());

        GeometryFactory geometryFactory = new GeometryFactory();
        Geometry geometry;

        if (geomSaveDto.getShape().equals("CIRCLE")) {
            Coordinate centerCoord = new Coordinate(
                    geomSaveDto.getCenter().getLongitude(),
                    geomSaveDto.getCenter().getLatitude()
            );
            geometry = geometryFactory.createPoint(centerCoord).buffer(geomSaveDto.getRadius());

        } else if (geomSaveDto.getShape().equals("POLYGON")) {
            if (isPolygonOpen(geomSaveDto)) {
                throw new OpenPolygonException();
            }
            List<Coordinate> coords = new ArrayList<>();
            for (CoordinateDto dotCoords : geomSaveDto.getCoordinates()) {
                coordinateValidator.validateCoordinate(
                        dotCoords.getLongitude(),
                        dotCoords.getLatitude()
                );
                coords.add(new Coordinate(dotCoords.getLongitude(), dotCoords.getLatitude()));
            }
            geometry = geometryFactory.createPolygon(coords.toArray(new Coordinate[0]));
        } else {
            throw new IllegalArgumentException("Formato de geometria invalido.");
        }

        location.setGeom(geometry);
        Location savedLocation = locationRepository.save(location);
        return buildLocationDto(savedLocation);
    }

    @Transactional
    @Override
    public String deleteLocation(Long id) {
        locationRepository.deleteById(id);
        return "Local com id " + id + " deletado com sucesso.";
    }

    private List<CoordinateDto> convertGeometryToCoordinateList(Geometry geometry) {
        List<CoordinateDto> coordinateDtoList = new ArrayList<>();
        if (geometry != null) {
            for (Coordinate coordinate : geometry.getCoordinates()) {
                coordinateDtoList.add(new CoordinateDto(coordinate.getX(), coordinate.getY()));
            }
        }
        return coordinateDtoList;
    }

    private boolean isPolygonOpen(GeomSaveDto saveDto) {
        int lastIndex = saveDto.getCoordinates().size()-1;
        if (saveDto.getCoordinates().get(0).getLatitude() != saveDto.getCoordinates().get(lastIndex).getLatitude()
        || saveDto.getCoordinates().get(0).getLongitude() != saveDto.getCoordinates().get(lastIndex).getLongitude()
        ) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isCircle(LinearRing ring) {
        Coordinate centroid = ring.getCentroid().getCoordinate();
        double radius = centroid.distance(ring.getCoordinateN(0));
        double tolerance = 0.01 * radius; // 1%

        for (int i = 0; i < ring.getNumPoints(); i++) {
            double distance = centroid.distance(ring.getCoordinateN(i));
            if (Math.abs(distance - radius) > tolerance) {
                return false;
            }
        }
        return true;
    }

    private LocationDto buildLocationDto(Location location) {
        Geometry geometry = location.getGeom();
        LocationDto.LocationDtoBuilder dtoBuilder = LocationDto.builder()
                .idLocation(location.getIdLocation())
                .name(location.getName());

        if (geometry instanceof Polygon polygon) {
            LinearRing shell = (LinearRing) polygon.getExteriorRing();

            if (isCircle(shell)) {
                Coordinate centerCoord = polygon.getCentroid().getCoordinate();
                double radius = centerCoord.distance(shell.getCoordinateN(0));

                double roundedCenterX = round(centerCoord.x, 8);
                double roundedCenterY = round(centerCoord.y, 8);
                double roundedRadius = round(radius, 8);

                dtoBuilder.shape("CIRCLE")
                        .center(new CoordinateDto(roundedCenterX, roundedCenterY))
                        .radius(roundedRadius);
            } else {
                dtoBuilder.shape("POLYGON")
                        .coordinates(convertGeometryToCoordinateList(polygon));
            }
        }

        return dtoBuilder.build();
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }}
