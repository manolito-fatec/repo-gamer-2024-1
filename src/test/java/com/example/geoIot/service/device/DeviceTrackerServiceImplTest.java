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
import com.example.geoIot.service.person.PersonService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DeviceTrackerServiceImplTest {

    @InjectMocks
    private DeviceTrackerServiceImpl deviceService;
    @Mock
    private PersonRepository personRepository;
    @Mock
    private DeviceTrackerRepository deviceTrackerRepository;
    @Mock
    private List<DeviceTracker> deviceTracker;
    @Mock
    private PersonService personService;

    private Person person = new Person(
            1l,
            "72E66DD7-DC7B-4C6E-B4F4-328E256DF9BA",
            "VALDEMIR OLIVEIRA DOS SANTOS",
            "4185");

    private DeviceTracker deviceTracker1 = new DeviceTracker(
            null,
            "1640966C-BBAC-4A26-8A06-0670296D361C",
            LocalDateTime.now(),
            0.784053,
            -60.457873,
            person
    );
    private DeviceTracker deviceTracker2 = new DeviceTracker(
            null,
            "53CA1452-77FF-4B77-94CF-0037B3C00054",
            LocalDateTime.now().plusMinutes(13),
            0.5840540000,
            -60.4578737000,
            person
    );
    private DeviceTracker deviceTracker3 = new DeviceTracker(
            null,
            "DD287909-46BE-4E8B-A926-0056596288B1",
            LocalDateTime.now().plusMinutes(30),
            0.5840530000,
            -60.4578732000,
            person
    );

    private DeviceTracker deviceTracker4 =  new DeviceTracker(
            1l,
            "Fatec address.",
            LocalDateTime.now(),
            -23.1623982,
            -45.7947135,
            person
    );

    private DeviceTracker deviceTracker5 =  new DeviceTracker(
            1l,
            "DD287909-46BE-4E8B-A926-0056596288B2",
            LocalDateTime.now().plusMinutes(16),
            -23.1623982,
            -45.7947134,
            person
    );

    @Test
    @DisplayName("should register a device tracker.")
    void shouldRegisterPerson(){
        List<DeviceTracker> listOfDeviceTrackers = new ArrayList<>();
        listOfDeviceTrackers.add(deviceTracker1);
        listOfDeviceTrackers.add(deviceTracker2);
        listOfDeviceTrackers.add(deviceTracker3);
        BDDMockito.given(personRepository.findByIdText(person.getIdText())).willReturn(Optional.of(person));
        assertDoesNotThrow(() -> deviceService.saveDeviceTracker(listOfDeviceTrackers));
    }

    @Test
    @DisplayName("Should throw an exception when the person not exists in the system.")
    void exceptionShouldThrowAnExceptionWhenThePersonNotExistsInSystem(){
        List<DeviceTracker> listOfDeviceTrackers = new ArrayList<>();
        listOfDeviceTrackers.add(deviceTracker1);
        listOfDeviceTrackers.add(deviceTracker2);
        listOfDeviceTrackers.add(deviceTracker3);
        BDDMockito.given(personRepository.findByIdText(person.getIdText())).willReturn(Optional.empty());
        assertThrowsExactly(PersonNotFoundException.class, () ->deviceService.saveDeviceTracker(listOfDeviceTrackers));
    }

    @Test
    @DisplayName("Should throw an exception when any data of the DeviceTracker is invalid.")
    void exceptionShouldThrowAnExceptionWhenAnyDataOfTheDeviceTrackerIsInvalid(){
        List<DeviceTracker> listOfDeviceTrackers = new ArrayList<>();
        listOfDeviceTrackers.add(new DeviceTracker(
                null,
                "DD287909-46BE-4E8B-A926-0056596288B1",
                null,
                0.5840530000,
                -60.4578730000,
                person
        ));
        BDDMockito.given(personRepository.findByFullName(person.getFullName())).willReturn(Optional.of(person));
        assertThrowsExactly(IllegalArgumentException.class, () ->deviceService.saveDeviceTracker(listOfDeviceTrackers));
    }

    @Test
    @DisplayName("Should return a Page of DeviceTracker entries between the given dates")
    void getDeviceTrackerListByPeriod(){
        LocalDateTime init = LocalDateTime.of(2024,9,1,0,0);
        LocalDateTime end = LocalDateTime.of(2024,9,7,23,59);

        List<DeviceTracker> listOfDeviceTrackers = new ArrayList<>();
        listOfDeviceTrackers.add(deviceTracker1);
        listOfDeviceTrackers.add(deviceTracker2);
        listOfDeviceTrackers.add(deviceTracker3);

        Page<DeviceTracker> deviceTrackerPage = new PageImpl<>(listOfDeviceTrackers);
        BDDMockito.given(deviceTrackerRepository
                        .findByPersonDeviceTrackerIdPersonAndCreatedAtDeviceTrackerBetweenOrderByCreatedAtDeviceTracker(
                person.getIdPerson(), init, end, Pageable.unpaged()))
                .willReturn(deviceTrackerPage);

        DeviceTrackerPeriodRequestDto requestDto = DeviceTrackerPeriodRequestDto.builder()
                .personId(person.getIdPerson())
                .init(init)
                .end(end)
                .build();
        Page<DeviceTrackerDto> result = deviceService.getDeviceTrackerByDateInterval(requestDto, Pageable.unpaged());

        // Expected quantity & notNull verification
        assertNotNull(result);
        assertEquals(3, result.getContent().size());

        // Content test between Entity in Mockito & DTO
        DeviceTrackerDto deviceTrackerDto = result.getContent().get(0);
        assertEquals(deviceTracker1.getIdDeviceTracker(), deviceTrackerDto.getId());
        assertEquals(deviceTracker1.getLatitude(), deviceTrackerDto.getLatitude());
        assertEquals(deviceTracker1.getLongitude(), deviceTrackerDto.getLongitude());
    }

    @Test
    @DisplayName("Should return a List  of History entries between the given dates")
    void getDeviceHistoryByPeriod(){
        List<DeviceTracker> listOfDeviceTrackersToHistory = new ArrayList<>();
        listOfDeviceTrackersToHistory.add(deviceTracker4);
        listOfDeviceTrackersToHistory.add(deviceTracker5);
        assertFalse(deviceService.generateHistoryDto(listOfDeviceTrackersToHistory).isEmpty());
    }

    @Test
    @DisplayName("should perform a calculation and return a value.")
    void calculateLatitudeAndLongitude(){
        assertEquals(10.197810919275682, deviceService.calculateDistance(deviceTracker4,deviceTracker5));
    }

    @Test
    @DisplayName("should check if the distance between two points is less than 10")
    void deltaSpace(){
        assertEquals(true,deviceService.deltaSpaceIsValid(deviceTracker4,deviceTracker5));
    }

    @Test
    @DisplayName(" should check if the distance between two points is greater than 10.")
    void notDeltaSpace(){
        assertEquals(false,deviceService.deltaSpaceIsValid(deviceTracker1,deviceTracker2));
    }

    @Test
    @DisplayName("Should return data from an address.")
    void getAddress(){
        assertEquals("FATEC - SJC", deviceService.getAddress(deviceTracker4.getLatitude(), deviceTracker4.getLongitude()).getName());
    }

    @Test
    @DisplayName("Should return true if time difference is more that 15 min")
    void deltaTimeDifference(){
        assertEquals(true, deviceService.deltaTimeIsValid(deviceTracker1, deviceTracker3));
    }

    @Test
    @DisplayName("Should return true if time difference is less that 15 min")
    void notDeltaTimeDifference(){
        assertEquals(false, deviceService.deltaTimeIsValid(deviceTracker1, deviceTracker2));
    }
}