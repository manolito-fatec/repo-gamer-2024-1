package com.example.geoIot.service.device;

import com.example.geoIot.exception.EmptyDataListInRedisException;
import com.example.geoIot.entity.DeviceTracker;
import com.example.geoIot.entity.DeviceTrackerRedis;
import com.example.geoIot.entity.dto.DeviceTrackerRedisDto;
import com.example.geoIot.repository.DeviceTrackerRedisRepository;
import com.example.geoIot.service.person.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@EnableScheduling
public class DeviceTrackerRedisServiceImpl implements DeviceTrackerRedisService {


    private final long MINUTES = 1000 * 60;

    @Autowired
    private DeviceTrackerRedisRepository deviceTrackerRedisRepository;


    @Autowired
    private PersonService personService;

    @Autowired
    private DeviceTrackerService deviceTrackerService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @Override
    public void saveDataInCache(List<DeviceTrackerRedisDto> pDeviceTrackerRedis) {
        if (pDeviceTrackerRedis.isEmpty()) {
            throw new EmptyDataListInRedisException();
        }
        List<DeviceTrackerRedis> listConverted = this.convertToDeviceTrackerListRedis(pDeviceTrackerRedis);
        deviceTrackerRedisRepository.saveAll(listConverted);
        this.synchronizeDataBase();
    }

    @Override
//  @Scheduled(fixedDelay = MINUTES)
    public void synchronizeDataBase() {
        List<DeviceTrackerRedis> deviceTrackerRedisList = this.findAllInRedis();

       if(deviceTrackerRedisList == null || deviceTrackerRedisList.isEmpty()){
           throw new EmptyDataListInRedisException();
       }
       List<DeviceTracker> deviceTrackerList = this.convertToDeviceTrackerList(deviceTrackerRedisList);
       this.deviceTrackerService.saveDeviceTracker(deviceTrackerList);
       this.deviceTrackerRedisRepository.deleteAll();
    }

    public List<DeviceTrackerRedis> findAllInRedis(){
        return (List<DeviceTrackerRedis>)deviceTrackerRedisRepository.findAll();
    }

    public List<DeviceTrackerRedis> convertToDeviceTrackerListRedis(List<DeviceTrackerRedisDto> pDeviceTrackerRedis) {
        return pDeviceTrackerRedis.stream()
                .map(deviceTrackerRedisDto -> {
                    DeviceTrackerRedis deviceConvert = new DeviceTrackerRedis();
                    deviceConvert.setIdTextDeviceTracker(deviceTrackerRedisDto.Id());
                    deviceConvert.setCreatedDeviceTracker(LocalDateTime.parse(deviceTrackerRedisDto.CreatedAt(), formatter));
                    deviceConvert.setLatitude(BigDecimal.valueOf(deviceTrackerRedisDto.Latitude()));
                    deviceConvert.setLongitude(BigDecimal.valueOf(deviceTrackerRedisDto.Longitude()));
                    deviceConvert.setFullName(personService.findByFullName(deviceTrackerRedisDto.FullName()));
                    return deviceConvert;
                })
                .collect(Collectors.toList());
    }

    public List<DeviceTracker> convertToDeviceTrackerList( List<DeviceTrackerRedis> pDeviceTrackerRedis){
        return pDeviceTrackerRedis.stream()
                .map(deviceTrackerRedis-> {
                    DeviceTracker deviceConvert = new DeviceTracker();
                    deviceConvert.setIdTextDeviceTracker(deviceTrackerRedis.getIdTextDeviceTracker());
                    deviceConvert.setCreatedDeviceTracker(deviceTrackerRedis.getCreatedDeviceTracker());
                    deviceConvert.setLatitude(deviceTrackerRedis.getLatitude());
                    deviceConvert.setLongitude(deviceTrackerRedis.getLongitude());
                    deviceConvert.setPersonDeviceTracker(deviceTrackerRedis.getFullName());
                    return deviceConvert;
                })
                .collect(Collectors.toList());
    }
}
