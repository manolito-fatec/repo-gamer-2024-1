package com.example.geoIot.service.device;

import com.example.geoIot.entity.Person;
import com.example.geoIot.exception.EmptyDataListInRedisException;
import com.example.geoIot.entity.DeviceTracker;
import com.example.geoIot.entity.DeviceTrackerRedis;
import com.example.geoIot.entity.dto.DeviceTrackerRedisDto;
import com.example.geoIot.exception.LatitudeValueException;
import com.example.geoIot.exception.LongitudeValueException;
import com.example.geoIot.exception.PersonNotFoundException;
import com.example.geoIot.repository.DeviceTrackerRedisRepository;
import com.example.geoIot.service.person.PersonService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@EnableScheduling
public class DeviceTrackerRedisServiceImpl implements DeviceTrackerRedisService{

    private final long MINUTES = 1000 * 60;

    @Autowired
    private DeviceTrackerRedisRepository deviceTrackerRedisRepository;

    @Autowired
    private PersonService personService;

    @Autowired
    private DeviceTrackerService deviceTrackerService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @Getter
    private  Set<Person> personSet;

    @PostConstruct
    public void setPersonInit(){
        this.personSet = personService.getAllPersons();
    }

    @Override
    public void saveDataInCache(List<DeviceTrackerRedisDto> pDeviceTrackerRedis) {
        if (pDeviceTrackerRedis.isEmpty()) {
            throw new EmptyDataListInRedisException();
        }
        List<DeviceTrackerRedis> listConverted = this.convertToDeviceTrackerListRedis(pDeviceTrackerRedis,personSet);
        deviceTrackerRedisRepository.saveAll(listConverted);
    }

    @Override
    @Scheduled(fixedDelay = 5 * MINUTES)
    public void synchronizeDataBase() {
       List<DeviceTrackerRedis> deviceTrackerRedisList = this.findAllInRedis();
       if(!(deviceTrackerRedisList == null) || !(deviceTrackerRedisList.isEmpty())){
           List<DeviceTracker> deviceTrackerList = this.convertToDeviceTrackerList(deviceTrackerRedisList);
           this.deviceTrackerService.saveDeviceTracker(deviceTrackerList);
           this.deviceTrackerRedisRepository.deleteAll();
       }
    }

    public List<DeviceTrackerRedis> findAllInRedis(){
        return (List<DeviceTrackerRedis>)deviceTrackerRedisRepository.findAll();
    }

    public List<DeviceTrackerRedis> convertToDeviceTrackerListRedis(List<DeviceTrackerRedisDto> pDeviceTrackerRedis, Set<Person> pPersonSet) {
        return pDeviceTrackerRedis.stream()
                .map(deviceTrackerRedisDto -> {
                    DeviceTrackerRedis deviceConvert = new DeviceTrackerRedis();
                    deviceConvert.setIdTextDeviceTracker(deviceTrackerRedisDto.Id());
                    deviceConvert.setCreatedAtDeviceTracker(LocalDateTime.parse(deviceTrackerRedisDto.CreatedAt(), formatter));
                    deviceConvert.setLatitude(this.latitudeValidate(deviceTrackerRedisDto.Latitude()));
                    deviceConvert.setLongitude(this.longitudeValidate(deviceTrackerRedisDto.Longitude()));
                    deviceConvert.setFullName(this.convertToPerson(deviceTrackerRedisDto.FullName(), pPersonSet));
                    return deviceConvert;
                })
                .collect(Collectors.toList());
    }

    protected Person convertToPerson(String pPersonName,Set<Person> pPersonSet) {
        Person personConvert = pPersonSet.stream()
                .filter(p -> p.getFullName().equals(pPersonName))
                .findFirst()
                .orElse(null);
        if(personConvert == null) {
            throw new PersonNotFoundException();
        }
        return personConvert;
    }

    public List<DeviceTracker> convertToDeviceTrackerList( List<DeviceTrackerRedis> pDeviceTrackerRedis){
        return pDeviceTrackerRedis.stream()
                .map(deviceTrackerRedis-> {
                    DeviceTracker deviceConvert = new DeviceTracker();
                    deviceConvert.setIdTextDeviceTracker(deviceTrackerRedis.getIdTextDeviceTracker());
                    deviceConvert.setCreatedAtDeviceTracker(deviceTrackerRedis.getCreatedAtDeviceTracker());
                    deviceConvert.setLatitude(deviceTrackerRedis.getLatitude());
                    deviceConvert.setLongitude(deviceTrackerRedis.getLongitude());
                    deviceConvert.setPersonDeviceTracker(deviceTrackerRedis.getFullName());
                    return deviceConvert;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void onEvent() {
        this.personSet = (personService.getAllPersons() != null)
                ? personService.getAllPersons()
                : this.personSet;
    }

   protected Double latitudeValidate(Double pLatitude){
        if(pLatitude == null) {
            throw new RuntimeException("Latitude is null");
        }
       Double MIN_LATITUDE = -90.0;
       Double MAX_LATITUDE = 90.0;
       if (pLatitude.compareTo(MIN_LATITUDE) >= 0 && pLatitude.compareTo(MAX_LATITUDE) <= 0){
            return pLatitude;
        }
        throw new LatitudeValueException();
   }

    protected Double longitudeValidate(Double pLongitude){
        if(pLongitude == null) {
            throw new RuntimeException("Longitude is null");
        }
        Double MIN_LONGITUDE =  -180.0;
        Double MAX_LONGITUDE = 180.0;
        if (pLongitude.compareTo(MIN_LONGITUDE) >= 0 && pLongitude.compareTo(MAX_LONGITUDE) <= 0){
            return pLongitude;
        }
        throw new LongitudeValueException();
    }
}
