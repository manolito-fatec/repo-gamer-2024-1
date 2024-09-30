package com.example.geoIot.service.device;

import com.example.geoIot.component.personList.StartupPersonListComponent;
import com.example.geoIot.entity.DeviceTracker;
import com.example.geoIot.entity.DeviceTrackerRedis;
import com.example.geoIot.entity.Person;
import com.example.geoIot.entity.dto.DeviceTrackerRedisDto;
import com.example.geoIot.exception.EmptyDataListInRedisException;
import com.example.geoIot.exception.PersonNotFoundException;
import com.example.geoIot.service.person.PersonService;

import com.example.geoIot.service.person.PersonServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeviceTrackerRedisServiceImplTest {
    @InjectMocks
    private DeviceTrackerRedisServiceImpl deviceServiceRedis;
    @Mock
    private PersonService personService;

    @Mock
    private PersonServiceImpl personServiceToUpdated;

    private Person person = new Person(
            1l,
                    "72E66DD7-DC7B-4C6E-B4F4-328E256DF9BA",
                    "VALDEMIR OLIVEIRA DOS SANTOS",
                    "4185"
    );

    private Person person2 = new Person(
            2l,
            "72705EC2-308D-4665-8132-25368D1D13C7",
            "ESTER DE FREITAS DE LIMA",
            "4185"
    );

    private Person person3 = new Person(
            3l,
            "04B6C39D-1C3B-4EED-BD0D-0701B94FB5AEA",
            "Antônio Carlos Batista Ferreira Brito",
            "Card_506A"
    );


    private DeviceTrackerRedisDto dto1 = new DeviceTrackerRedisDto(
            "53CA1452-77FF-4B77-94CF-0037B3C00054",
            "2024-05-09T14:22:43.707",
            0.5840530000,
            -60.4578730000,
            "VALDEMIR OLIVEIRA DOS SANTOS",
            "4185"
    );

    private DeviceTrackerRedisDto dto2 = new DeviceTrackerRedisDto(
            "DD287909-46BE-4E8B-A926-0056596288B1",
            "2024-05-09T14:22:43.707",
            0.5840530000,
            -60.4578730000,
            "VALDEMIR OLIVEIRA DOS SANTOS",
            "4185"
    );

    private DeviceTrackerRedis dto3 = new DeviceTrackerRedis(
            "53CA1452-77FF-4B77-94CF-0037B3C00054",
            LocalDateTime.now(),
            BigDecimal.valueOf(0.5840530000),
            BigDecimal.valueOf(-60.4578730000),
            person
    );

    private DeviceTrackerRedis dto4 = new DeviceTrackerRedis(
            "DD287909-46BE-4E8B-A926-0056596288B1",
            LocalDateTime.now(),
            BigDecimal.valueOf(0.5840530000),
            BigDecimal.valueOf(-60.4578730000),
            person
    );


    @Test
    @DisplayName("should throw the EmptyDataListInRedisException exception")
    void shouldThrowEmptyDataListInRedisException() {
        assertThrows(EmptyDataListInRedisException.class, () -> {deviceServiceRedis.saveDataInCache(new ArrayList<>());});
    }

    @Test
    @DisplayName("Should convert the list of DeviceTrackerRedisDto to a list of DeviceTrackerRedis")
    void shouldConvertListOfDeviceTrackerRedisDtoToListOfDeviceTrackerRedis() {
        List<DeviceTrackerRedisDto> dtoList = new ArrayList<>();
        dtoList.add(dto1);
        dtoList.add(dto2);
        Set<Person> personSet = new HashSet<>();
        personSet.add(person);
        personSet.add(person2);
        personSet.add(person3);
        String namePerson = "VALDEMIR OLIVEIRA DOS SANTOS";

        assertInstanceOf(DeviceTrackerRedis.class, deviceServiceRedis.convertToDeviceTrackerListRedis(dtoList,personSet).get(0));
    }

    @Test
    @DisplayName("Should convert the list of DeviceTrackerRedis to a list of DeviceTracker")
    void shouldConvertListOfDeviceTrackerRedisToListOfDeviceTracker() {
        List<DeviceTrackerRedis> dtoList = new ArrayList<>();
        dtoList.add(dto3);
        dtoList.add(dto4);

        assertInstanceOf(DeviceTracker.class, deviceServiceRedis.convertToDeviceTrackerList(dtoList).get(0));
    }

    @Test
    @DisplayName("Should get Person in list")
    void shouldGetPersonInList() {
        Set<Person> personSet = new HashSet<>();
        personSet.add(person);
        personSet.add(person2);
        personSet.add(person3);
        String namePerson = "VALDEMIR OLIVEIRA DOS SANTOS";

       assertEquals(deviceServiceRedis.convertToPerson(namePerson, personSet), person);
    }

    @Test
    @DisplayName("Should throw the PersonNotFoundException when name not exist")
    void shouldThrowPersonNotFoundExceptionWhenNameNotExist() {
        Set<Person> personSet = new HashSet<>();
        personSet.add(person);
        personSet.add(person2);
        personSet.add(person3);
        String namePerson = "Bob Esponja";

        assertThrows(PersonNotFoundException.class, ()->deviceServiceRedis.convertToPerson(namePerson, personSet));
    }

    @Test
    @DisplayName("should update the people in the list with the default list if getAllPersons() is null.")
    void shouldUpdatePeopleSet() {
        BDDMockito.given(personService.getAllPersons()).willReturn(null);

        deviceServiceRedis.onEvent();
        assertEquals(StartupPersonListComponent.getPersons(), personService.getAllPersons());
    }

    @Test
    @DisplayName("should update the people in the list with the list with the value of getAllPersons().")
    void shouldUpdatePeopleSetWithValue() {
        Set<Person> personSet = new HashSet<>();
        personSet.add(person);
        personSet.add(person2);
        BDDMockito.given(personService.getAllPersons()).willReturn(personSet);

        deviceServiceRedis.onEvent();
        assertEquals(personSet, personService.getAllPersons());
    }
}