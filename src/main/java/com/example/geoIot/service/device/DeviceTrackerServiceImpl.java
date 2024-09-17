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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public List<DeviceTrackerDto> getDeviceTrackerByDateInterval(DeviceTrackerPeriodRequestDto requestDto) {
        Optional<List<DeviceTracker>> deviceTrackerList = deviceTrackerRepository
                .findByPersonDeviceTrackerIdAndCreatedAtDeviceTrackerBetween(
                        requestDto.getPersonId(),
                        requestDto.getInit(),
                        requestDto.getEnd());
        if (deviceTrackerList.isEmpty()) {
            throw new NoSuchElementException("No data available for the requested period.");
        } else {
            List<DeviceTrackerDto> dtoList = new ArrayList<>();
            for (DeviceTracker deviceTracker : deviceTrackerList.get()) {
                dtoList.add(this.dtoConverter(deviceTracker));
            }
            return dtoList;
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
