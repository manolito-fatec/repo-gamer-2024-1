package com.example.geoIot.service.device;

import com.example.geoIot.entity.dto.DeviceTrackerDto;
import com.example.geoIot.entity.dto.DeviceTrackerPeriodRequestDto;
import com.example.geoIot.entity.dto.history.HistoryDto;
import com.example.geoIot.entity.dto.history.LocationDto;
import com.example.geoIot.entity.dto.history.StopDto;
import com.example.geoIot.exception.PersonNotFoundException;
import com.example.geoIot.entity.DeviceTracker;
import com.example.geoIot.entity.Person;
import com.example.geoIot.repository.DeviceTrackerRepository;
import com.example.geoIot.repository.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DeviceTrackerServiceImpl implements DeviceTrackerService {

    @Autowired
    private DeviceTrackerRepository deviceTrackerRepository;

    @Autowired
    private PersonRepository personRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final Double DISTANCE_THRESHOLD = 5.0;

    private final Long TIME_THRESHOLD_MINUTES = 15L;

    private final Double EARTH_RADIUS = 6371000.0;

    private final String BASEURL = "https://nominatim.openstreetmap.org/";


    @Transactional
    @Override
    public void saveDeviceTracker(List<DeviceTracker> pDeviceTracker) {
        pDeviceTracker.forEach(deviceTracker -> {
            this.validateDeviceTracker(deviceTracker);
            this.validatePersonAlreadyExists(deviceTracker.getPersonDeviceTracker());
        });
        this.deviceTrackerRepository.saveAll(pDeviceTracker);
    }

    @Transactional
    @Override
    public Page<DeviceTrackerDto> getDeviceTrackerByDateInterval(DeviceTrackerPeriodRequestDto requestDto, Pageable pageable) {
        Page<DeviceTracker> deviceTrackerPage = deviceTrackerRepository
                .findByPersonDeviceTrackerIdPersonAndCreatedAtDeviceTrackerBetweenOrderByCreatedAtDeviceTracker(
                        requestDto.getPersonId(),
                        requestDto.getInit(),
                        requestDto.getEnd(),
                        pageable
                );
        if (deviceTrackerPage.isEmpty()) {
            throw new NoSuchElementException("No data available for the requested period.");
        } else {
            return deviceTrackerPage.map(this::dtoConverter);
        }
    }

    @Override
    public Page<HistoryDto> searchHistoryByDateInterval(DeviceTrackerPeriodRequestDto pRequestDto, Pageable pageable) {
        Long personId = pRequestDto.getPersonId();
        LocalDateTime initDate = LocalDateTime.parse(pRequestDto.getInit().toString(), formatter);
        LocalDateTime endDate = LocalDateTime.parse(pRequestDto.getEnd().toString(), formatter);
        Page<DeviceTracker> deviceTrackers = deviceTrackerRepository.filterToHistoric(personId, initDate, endDate, pageable);
        Long totalNumberOfPoints = deviceTrackerRepository.countPoint(personId, initDate, endDate);
        List<HistoryDto> historic = generateHistoryDto(deviceTrackers.getContent());
        return new PageImpl<>(historic, pageable,totalNumberOfPoints);
    }

    @Override
    public List<DeviceTrackerDto> getTrackersInsideLocation(Long locationId, LocalDateTime init, LocalDateTime end, Long userId) {
        List<DeviceTracker> deviceTrackerList = deviceTrackerRepository.findTrackersInsideLocation(locationId, init, end, userId);
        return deviceTrackerList.stream()
                .map(this::dtoConverter)
                .toList();
    }

    protected List<HistoryDto> generateHistoryDto(List<DeviceTracker> pDeviceTrackers) {
        List<HistoryDto> historic = new ArrayList<>();
        if(!pDeviceTrackers.isEmpty()){
            StopDto firstStop = generateStop(pDeviceTrackers.get(0));
            StopDto lastStop = generateStop(pDeviceTrackers.get(pDeviceTrackers.size() - 1));
            List<StopDto> stops = generateListStop(pDeviceTrackers);
            stops.add(0,firstStop);
            stops.add(lastStop);
            for (int i = 0; i < stops.size() - 1; i++) {
                StopDto startStop = stops.get(i);
                StopDto endStop = stops.get(i + 1);
                HistoryDto newHistory = generateTravel(startStop,endStop);
                historic.add(newHistory);
            }
        }
        return historic;
    }

    private HistoryDto  generateTravel(StopDto pStop1, StopDto pStop2) {
        LocationDto locationFirstPoint = getAddress(pStop2.getLatitude(), pStop2.getLongitude());
        LocationDto locationSecondPoint = getAddress(pStop1.getLatitude(), pStop1.getLongitude());
        LocalDateTime dateTimeFirstPoint = pStop1.getTimestamp();
        LocalDateTime dateTimeSecondPoint = pStop2.getTimestamp();
        Double distanceBetweenPoints = calculateDistance(pStop1, pStop2);
        return new HistoryDto(
                dateTimeFirstPoint,
                dateTimeSecondPoint,
                distanceBetweenPoints,
                locationFirstPoint,
                locationSecondPoint
        );
    }

    private List<StopDto> generateListStop(List<DeviceTracker> pDeviceTrackers) {
        List<StopDto> stopList = new ArrayList<>();
        if (pDeviceTrackers != null || !pDeviceTrackers.isEmpty()) {
            DeviceTracker referencePoint = pDeviceTrackers.get(0);
            for (DeviceTracker pointTracker : pDeviceTrackers) {
                if (this.deltaSpaceIsValid(referencePoint, pointTracker)) {
                    if(this.deltaTimeIsValid(referencePoint, pointTracker)) {
                        StopDto newStop = this.generateStop(pointTracker);
                        stopList.add(newStop);
                        referencePoint = pointTracker;
                    }
                }else {
                    referencePoint = pointTracker;
                }
            }
        }
        return stopList;
    }

    private StopDto generateStop(DeviceTracker pDeviceTracker) {
        LocationDto location = getAddress(pDeviceTracker.getLatitude(), pDeviceTracker.getLongitude());
        return new StopDto(
                pDeviceTracker.getLatitude(),
                pDeviceTracker.getLongitude(),
                pDeviceTracker.getCreatedAtDeviceTracker(),
                location);
    }

    protected  Boolean deltaSpaceIsValid(DeviceTracker pPoint1, DeviceTracker pPoint2) {
        Double distance = calculateDistance(pPoint1, pPoint2);
        return  DISTANCE_THRESHOLD > distance;
    }

    protected Boolean deltaTimeIsValid(DeviceTracker pPoint1, DeviceTracker pPoint2) {
        Duration duration = Duration.between(pPoint1.getCreatedAtDeviceTracker(), pPoint2.getCreatedAtDeviceTracker());
        Long timeElapsed = duration.toMinutes();
        return timeElapsed > TIME_THRESHOLD_MINUTES;
    }

    protected Double calculateDistance(DeviceTracker pFirstPoint, DeviceTracker pSecondPoint) {
        Double lat1 = Math.toRadians(pFirstPoint.getLatitude());
        Double lon1 = Math.toRadians(pFirstPoint.getLongitude());
        Double lat2 = Math.toRadians(pSecondPoint.getLatitude());
        Double lon2 = Math.toRadians(pSecondPoint.getLongitude());

        Double dLat = lat2 - lat1;
        Double dLon = lon2 - lon1;

        Double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    protected Double calculateDistance(StopDto pFirstPoint, StopDto pSecondPoint) {
        Double lat1 = Math.toRadians(pFirstPoint.getLatitude());
        Double lon1 = Math.toRadians(pFirstPoint.getLongitude());
        Double lat2 = Math.toRadians(pSecondPoint.getLatitude());
        Double lon2 = Math.toRadians(pSecondPoint.getLongitude());

        Double dLat = lat2 - lat1;
        Double dLon = lon2 - lon1;

        Double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    protected LocationDto getAddress(Double pLatitude, Double pLongitude){
        RestClient restClient = RestClient.create();
        return restClient.get()
                .uri(BASEURL +"/reverse?lat={lat}&lon={lon}&accept-language=pt_br&format=json", pLatitude, pLongitude)
                .retrieve()
                .body(LocationDto.class);
    }

    private void validateDeviceTracker(DeviceTracker pDeviceTracker) {
        if(pDeviceTracker == null ||
           pDeviceTracker.getIdTextDeviceTracker() == null ||
           pDeviceTracker.getIdTextDeviceTracker().isEmpty()||
           pDeviceTracker.getCreatedAtDeviceTracker() == null||
           pDeviceTracker.getLatitude() == null||
           pDeviceTracker.getLongitude() == null){
           throw new IllegalArgumentException("Some field is null or empty.");
        }
    }

    private void validatePersonAlreadyExists(Person pPerson) {
        Optional<Person> pPersonFound = personRepository.findByIdText(pPerson.getIdText());
        if (pPersonFound.isEmpty()){
            throw new PersonNotFoundException(pPerson.getFullName());
        }
    }

    private DeviceTrackerDto dtoConverter(DeviceTracker deviceTracker) {
        return DeviceTrackerDto.builder()
                .id(deviceTracker.getIdDeviceTracker())
                .itoId(deviceTracker.getIdTextDeviceTracker())
                .createdAt(deviceTracker.getCreatedAtDeviceTracker())
                .latitude(deviceTracker.getLatitude())
                .longitude(deviceTracker.getLongitude())
                .build();
    }
}
