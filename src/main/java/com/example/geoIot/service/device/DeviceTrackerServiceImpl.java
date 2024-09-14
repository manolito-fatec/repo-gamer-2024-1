package com.example.geoIot.service.device;

import com.example.geoIot.Exception.PersonNotFoundException;
import com.example.geoIot.entity.DeviceTracker;
import com.example.geoIot.entity.Person;
import com.example.geoIot.repository.DeviceTrackerRepository;
import com.example.geoIot.repository.PersonRepository;
import com.example.geoIot.service.person.PersonService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeviceTrackerServiceImpl implements DeviceTrackerService {

    @Autowired
    private DeviceTrackerRepository deviceTrackerRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PersonService personService;

    @Transactional
    @Override
    public void saveDeviceTracker(DeviceTracker pDeviceTracker) {
        this.validateDeviceTracker(pDeviceTracker);
        this.validatePersonAlreadyExists(pDeviceTracker.getPersonDeviceTracker());
        this.deviceTrackerRepository.save(pDeviceTracker);
    }



    private void validateDeviceTracker(DeviceTracker pDeviceTracker) {
        if(pDeviceTracker == null ||
           pDeviceTracker.getIdTextDeviceTracker() == null ||
           pDeviceTracker.getIdTextDeviceTracker().isEmpty()||
           pDeviceTracker.getCreatedDeviceTracker() == null||
           pDeviceTracker.getLatitude() == null||
           pDeviceTracker.getLongitude() == null){
           throw new IllegalArgumentException("Some field is null or empty.");
        }
        this.personService.validatePerson(pDeviceTracker.getPersonDeviceTracker());
    }

    private void validatePersonAlreadyExists(Person pPerson) {
        Optional<Person> pPersonFound = personRepository.findByIdText(pPerson.getIdText());
        if (pPersonFound.isEmpty()){
            throw new PersonNotFoundException(pPerson.getFullName());
        }
    }
}
