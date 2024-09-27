package com.example.geoIot.service.device;

import com.example.geoIot.entity.dto.DeviceTrackerDto;
import com.example.geoIot.entity.dto.DeviceTrackerPeriodRequestDto;
import com.example.geoIot.exception.PersonNotFoundException;
import com.example.geoIot.entity.DeviceTracker;
import com.example.geoIot.entity.Person;
import com.example.geoIot.repository.DeviceTrackerRepository;
import com.example.geoIot.repository.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class DeviceTrackerServiceImpl implements DeviceTrackerService {

    @Autowired
    private DeviceTrackerRepository deviceTrackerRepository;

    @Autowired
    private PersonRepository personRepository;

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
