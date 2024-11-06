package com.example.geoIot.service.location;

import com.example.geoIot.entity.Location;
import com.example.geoIot.entity.dto.CoordinateDto;
import com.example.geoIot.entity.dto.LocationDto;
import com.example.geoIot.entity.dto.GeomSaveDto;
import com.example.geoIot.exception.OpenPolygonException;
import com.example.geoIot.repository.LocationRepository;
import com.example.geoIot.util.CoordinateValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.*;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class LocationServiceImplTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private CoordinateValidator coordinateValidator;

    @InjectMocks
    private LocationServiceImpl locationService;

    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Test
    @DisplayName("should return a LocationDto for a given ID")
    public void getLocation() {
        Location location = new Location();
        location.setIdLocation(1L);
        location.setName("Test Location");

        Polygon polygon = geometryFactory.createPolygon(new Coordinate[]{
                new Coordinate(10.0, 20.0),
                new Coordinate(15.0, 25.0),
                new Coordinate(20.0, 30.0),
                new Coordinate(10.0, 20.0)
        });
        polygon.setUserData("POLYGON");
        location.setGeom(polygon);

        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));

        LocationDto locationDto = locationService.getLocation(1L);

        assertNotNull(locationDto);
        assertEquals("Test Location", locationDto.getName());
        assertEquals("POLYGON", locationDto.getShape());
    }

    @Test
    @DisplayName("should throw NoSuchElementException when location ID is not found")
    public void getLocationNotFound() {
        when(locationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> locationService.getLocation(1L));
    }

    @Test
    @DisplayName("should return a list of all LocationDtos")
    public void getAllLocations() {
        List<Location> locations = new ArrayList<>();

        Polygon polygon1 = geometryFactory.createPolygon(new Coordinate[]{
                new Coordinate(10.0, 20.0),
                new Coordinate(15.0, 25.0),
                new Coordinate(20.0, 30.0),
                new Coordinate(10.0, 20.0)
        });
        polygon1.setUserData("POLYGON");

        Location location1 = new Location();
        location1.setIdLocation(1L);
        location1.setName("Location 1");
        location1.setGeom(polygon1);

        Point center = geometryFactory.createPoint(new Coordinate(30.0, 40.0));
        Geometry circle = center.buffer(5);
        circle.setUserData("CIRCLE");

        Location location2 = new Location();
        location2.setIdLocation(2L);
        location2.setName("Location 2");
        location2.setGeom(circle);

        locations.add(location1);
        locations.add(location2);

        when(locationRepository.findAll()).thenReturn(locations);

        List<LocationDto> locationDtos = locationService.getAllLocations();

        assertEquals(2, locationDtos.size());
        assertEquals("Location 1", locationDtos.get(0).getName());
        assertEquals("POLYGON", locationDtos.get(0).getShape());
        assertEquals("Location 2", locationDtos.get(1).getName());
        assertEquals("CIRCLE", locationDtos.get(1).getShape());
    }

    @Test
    @DisplayName("should throw NoSuchElementException when no locations exist")
    public void getAllLocationsNoLocations() {
        when(locationRepository.findAll()).thenReturn(new ArrayList<>());

        assertThrows(NoSuchElementException.class, () -> locationService.getAllLocations());
    }

    @Test
    @DisplayName("should save and return a LocationDto for a POLYGON")
    public void saveLocationPolygon() {
        GeomSaveDto geomSaveDto = new GeomSaveDto();
        geomSaveDto.setName("Polygon Location");
        geomSaveDto.setShape("POLYGON");
        geomSaveDto.setCoordinates(List.of(
                new CoordinateDto(10.0, 20.0),
                new CoordinateDto(15.0, 25.0),
                new CoordinateDto(20.0, 30.0),
                new CoordinateDto(10.0, 20.0)
        ));

        when(locationRepository.save(any(Location.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LocationDto savedLocation = locationService.saveLocation(geomSaveDto);

        assertNotNull(savedLocation);
        assertEquals("Polygon Location", savedLocation.getName());
        assertEquals("POLYGON", savedLocation.getShape());
    }

    @Test
    @DisplayName("should save and return a LocationDto for a CIRCLE")
    public void saveLocationCircle() {
        GeomSaveDto geomSaveDto = new GeomSaveDto();
        geomSaveDto.setName("Circle Location");
        geomSaveDto.setShape("CIRCLE");
        geomSaveDto.setCenter(new CoordinateDto(30.0, 40.0));
        geomSaveDto.setRadius(5.0);

        when(locationRepository.save(any(Location.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LocationDto savedLocation = locationService.saveLocation(geomSaveDto);

        assertNotNull(savedLocation);
        assertEquals("Circle Location", savedLocation.getName());
        assertEquals("CIRCLE", savedLocation.getShape());
        assertEquals(5.0, savedLocation.getRadius(), 1e-9); // Arredondado para 9 casas decimais pois é calculado de poligono
        assertEquals(30.0, savedLocation.getCenter().getLongitude(), 1e-9); // Arredondado para 9 casas decimais pois é calculado de poligono
        assertEquals(40.0, savedLocation.getCenter().getLatitude(), 1e-9); // Arredondado para 9 casas decimais pois é calculado de poligono
    }

    @Test
    @DisplayName("should throw IllegalArgumentException when saving a CIRCLE with invalid radius")
    public void saveLocationCircleInvalidRadius() {
        GeomSaveDto geomSaveDto = new GeomSaveDto();
        geomSaveDto.setName("Invalid Circle");
        geomSaveDto.setShape("CIRCLE");
        geomSaveDto.setCenter(new CoordinateDto(30.0, 40.0));
        geomSaveDto.setRadius(-1.0);

        assertThrows(IllegalArgumentException.class, () -> locationService.saveLocation(geomSaveDto));
    }

    @Test
    @DisplayName("should throw OpenPolygonException when saving an open POLYGON")
    public void saveLocationOpenPolygon() {
        GeomSaveDto geomSaveDto = new GeomSaveDto();
        geomSaveDto.setName("Open Polygon");
        geomSaveDto.setShape("POLYGON");
        geomSaveDto.setCoordinates(List.of(
                new CoordinateDto(10.0, 20.0),
                new CoordinateDto(15.0, 25.0),
                new CoordinateDto(20.0, 30.0)
        ));

        assertThrows(OpenPolygonException.class, () -> locationService.saveLocation(geomSaveDto));
    }
}
