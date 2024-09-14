package com.example.geoIot.service.device;

import com.example.geoIot.Exception.PersonNotFoundException;
import com.example.geoIot.entity.DeviceTracker;
import com.example.geoIot.entity.Person;
import com.example.geoIot.repository.DeviceTrackerRepository;
import com.example.geoIot.repository.PersonRepository;
import com.example.geoIot.service.person.PersonService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DeviceTrackerServiceImplTest {

    @InjectMocks
    private DeviceTrackerServiceImpl deviceService;
    @Mock
    private PersonRepository personRepository;
    @Mock
    private DeviceTrackerRepository deviceTrackerRepository;
    @Mock
    private DeviceTracker deviceTracker;
    @Mock
    private PersonService personService;

    private Person person = new Person( 1l, "1640966C-BBAC-4A26-8A06-0670296D361C", "Darwin Yoel Franco Vasquez", "1435");

    @Test
    @DisplayName("should register a device tracker.")
    void shouldRegisterPerson(){
        this.deviceTracker = new DeviceTracker(
                null,
                "1640966C-BBAC-4A26-8A06-0670296D361C",
                LocalDateTime.now(),
                new BigDecimal(0.5840530000),
                new BigDecimal(-60.4578730000),
                person
        );
        BDDMockito.given(personRepository.findByIdText(person.getIdText())).willReturn(Optional.of(person));
        assertDoesNotThrow(() -> deviceService.saveDeviceTracker(deviceTracker));
    }

    @Test
    @DisplayName("Should throw an exception when the person not exists in the system.")
    void exceptionShouldThrowAnExceptionWhenThePersonNotExistsInSystem(){
        this.deviceTracker = new DeviceTracker(
                null,
                "1640966C-BBAC-4A26-8A06-0670296D361C",
                LocalDateTime.now(),
                new BigDecimal(0.5840530000),
                new BigDecimal(-60.4578730000),
                person
        );
        BDDMockito.given(personRepository.findByIdText(person.getIdText())).willReturn(Optional.empty());
        assertThrowsExactly(PersonNotFoundException.class, () ->deviceService.saveDeviceTracker(deviceTracker));
    }

    @Test
    @DisplayName("Should throw an exception when any data of the DeviceTracker is invalid.")
    void exceptionShouldThrowAnExceptionWhenAnyDataOfTheDeviceTrackerIsInvalid(){
        this.deviceTracker = new DeviceTracker(
                null,
                "",
                LocalDateTime.now(),
                new BigDecimal(0.5840530000),
                new BigDecimal(-60.4578730000),
                person
        );
        assertThrowsExactly(IllegalArgumentException.class, () ->deviceService.saveDeviceTracker(this.deviceTracker));
    }


}