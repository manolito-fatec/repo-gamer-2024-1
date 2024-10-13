package com.example.geoIot.service.device;

import com.example.geoIot.entity.dto.DeviceTrackerDto;
import com.example.geoIot.entity.dto.DeviceTrackerPeriodRequestDto;
import com.example.geoIot.entity.dto.history.HistoryDto;
import com.example.geoIot.entity.dto.history.LocationDto;
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

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    private final Double DISTANCE_THRESHOLD = 10.0;

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
        List<HistoryDto> historic = generateHistoryDto(deviceTrackers.getContent());
        return new PageImpl<>(historic, pageable, historic.size());
    }

    private List<HistoryDto> generateHistoryDto(List<DeviceTracker> pDeviceTrackers) {
        Queue<DeviceTracker> queueOfStartTracker = new LinkedList<>(pDeviceTrackers);
        DeviceTracker firstPoint = queueOfStartTracker.poll();
        List<HistoryDto> historic = new ArrayList<>();
        if (firstPoint != null) {
            for (DeviceTracker pointTracker : queueOfStartTracker) {
                if (isStop(firstPoint, pointTracker)) {
                    LocationDto initialLocation = getAddress(firstPoint.getLatitude(), firstPoint.getLongitude());
                    LocationDto finalLocation = getAddress(pointTracker.getLatitude(), pointTracker.getLongitude());
                    Double distanceBetweenPoints = calculateDistance(firstPoint, pointTracker);
                    LocalDateTime endTime = firstPoint.getCreatedAtDeviceTracker();
                    LocalDateTime finalTime = pointTracker.getCreatedAtDeviceTracker();
                    historic.add(new HistoryDto(endTime, finalTime, distanceBetweenPoints,initialLocation, finalLocation));
                    firstPoint = pointTracker;
                }
            }
        }
        return historic;
    }

    private Boolean isStop(DeviceTracker pFirstPoint, DeviceTracker pPointTracker) {
        Duration duration = Duration.between(pFirstPoint.getCreatedAtDeviceTracker(), pPointTracker.getCreatedAtDeviceTracker());
        Long timeElapsed = duration.toMinutes();
        Double distance = calculateDistance(pFirstPoint, pPointTracker);
        return timeElapsed > TIME_THRESHOLD_MINUTES && distance >= DISTANCE_THRESHOLD;
    }

    private Double calculateDistance(DeviceTracker pFirstPoint, DeviceTracker pSecondPoint) {
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

    private LocationDto getAddress(Double pLatitude, Double pLongitude){
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
