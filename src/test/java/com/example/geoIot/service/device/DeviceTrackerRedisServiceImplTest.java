package com.example.geoIot.service.device;

import com.example.geoIot.entity.DeviceTracker;
import com.example.geoIot.entity.DeviceTrackerRedis;
import com.example.geoIot.entity.Person;
import com.example.geoIot.entity.dto.DeviceTrackerRedisDto;
import com.example.geoIot.exception.EmptyDataListInRedisException;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class DeviceTrackerRedisServiceImplTest {
    @InjectMocks
    private DeviceTrackerRedisServiceImpl deviceServiceRedis;
    @Mock
    private PersonService personService;

    private Person person = new Person(
            1l,
                    "72E66DD7-DC7B-4C6E-B4F4-328E256DF9BA",
                    "VALDEMIR OLIVEIRA DOS SANTOS",
                    "4185"
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

        BDDMockito.given(personService.findByFullName(person.getFullName())).willReturn(person);
        assertInstanceOf(DeviceTrackerRedis.class, deviceServiceRedis.convertToDeviceTrackerListRedis(dtoList).get(0));
    }

    @Test
    @DisplayName("Should convert the list of DeviceTrackerRedis to a list of DeviceTracker")
    void shouldConvertListOfDeviceTrackerRedisToListOfDeviceTracker() {
        List<DeviceTrackerRedis> dtoList = new ArrayList<>();
        dtoList.add(dto3);
        dtoList.add(dto4);

        assertInstanceOf(DeviceTracker.class, deviceServiceRedis.convertToDeviceTrackerList(dtoList).get(0));
    }

}